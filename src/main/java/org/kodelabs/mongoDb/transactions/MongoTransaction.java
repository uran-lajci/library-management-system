package org.kodelabs.mongoDb.transactions;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Shqiprim Bunjaku
 */
@Singleton
public class MongoTransaction {
    ReactiveMongoClient mongoClient;
    @Inject
    Logger logger;

    @Inject
    public MongoTransaction(ReactiveMongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    // TODO find a more stable solution for batch operations
    public <T> Uni<T> run(Function<MongoSession, Uni<? extends T>> updates) {
        return startSession()
                .flatMap(
                        clientSession ->
                                startTransaction(clientSession)
                                        .onItem()
                                        .transformToUni(updates)
                                        .onFailure(retryAfter())
                                        .retry()
                                        .atMost(10)
                                        .onItem()
                                        .transformToUni(entity -> commitTransaction(clientSession, entity)))
                .onFailure(retryAfter())
                .retry()
                .atMost(2)
                .onFailure()
                // log the error only if it failed after 10 times
                .invoke(throwable -> logger.error(throwable.getMessage(), throwable));
    }

    public Uni<MongoSession> startTransaction(MongoSession clientSession) {
        if (clientSession.unWrap().hasActiveTransaction()) {
            return Uni.createFrom()
                    .publisher(clientSession.abortTransaction())
                    .map(
                            unused -> {
                                clientSession.startTransaction();
                                return clientSession;
                            });
        }
        clientSession.startTransaction();
        return Uni.createFrom().item(clientSession);
    }

    private Uni<MongoSession> startSession() {
        return this.mongoClient.startSession().onItem().transform(MongoSession::new);
    }

    public <T> Uni<T> commitTransaction(MongoSession clientSession, T entity) {
        return Uni.createFrom()
                .publisher(clientSession.commitTransaction())
                .onFailure(retryAfter())
                .retry()
                .atMost(10)
                .onFailure()
                .invoke(throwable -> logger.error(throwable.getMessage(), throwable))
                .onItem()
                .transform(unused -> entity)
                .eventually(clientSession::close);
    }

    public Predicate<Throwable> retryAfter() {
        return throwable -> {
            logger.error(throwable.getMessage(), throwable);
            if (throwable instanceof MongoCommandException) {
                return checkMongoException(throwable);
            }

            if (throwable instanceof MongoQueryException) {
                MongoQueryException mongoQueryException = (MongoQueryException) throwable;
                // error code 251 -> no such transaction
                return mongoQueryException.getErrorCode() == 251;
            }

            if (throwable instanceof CompositeException) {
                List<Throwable> causes = ((CompositeException) throwable).getCauses();
                return causes.stream().anyMatch(this::checkMongoException);
            }

            return false;
        };
    }

    public boolean checkMongoException(Throwable throwable) {
        if (throwable instanceof MongoCommandException) {
            MongoCommandException mongoCommandException = (MongoCommandException) throwable;
            return mongoCommandException.hasErrorLabel(
                    MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL)
                    || mongoCommandException
                    .getErrorLabels()
                    .contains(MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL)
                    // error code 251 -> no such transaction
                    || mongoCommandException.getErrorCode() == 251;
        }
        return false;
    }
}
