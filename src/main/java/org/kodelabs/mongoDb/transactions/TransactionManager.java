package org.kodelabs.mongoDb.transactions;

import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.MongoQueryException;
import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Singleton
public class TransactionManager {
    ReactiveMongoClient mongoClient;

    @Inject
    public TransactionManager(ReactiveMongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Uni<MongoSession> startSession() {
        return mongoClient.startSession().onItem().transform(MongoSession::new);
    }

    public TransactionEntity<Void> startTransaction() {
        Uni<Tuple2<MongoSession, Void>> initialState = startSession()
                .flatMap(this::startTransaction)
                .map(mongoSession -> Tuple2.of(mongoSession, null));

        return new TransactionEntity<>(initialState);
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

    public static class TransactionEntity<T> {
        public Uni<Tuple2<MongoSession, T>> transactionState;

        @Inject
        Logger logger;

        public TransactionEntity(Uni<Tuple2<MongoSession, T>> transactionState) {
            this.transactionState = transactionState;
        }

        public <T2> TransactionEntity<T2> appendOperation(Function<Tuple2<MongoSession, T>, Uni<? extends T2>> operation) {
            Uni<Tuple2<MongoSession, T2>> stateAfterTransformation =
                    transactionState.flatMap(previousOpResult -> operation.apply(previousOpResult)
                            .map(resultAfterOp2 -> Tuple2.of(previousOpResult.getItem1(), resultAfterOp2)));
            return new TransactionEntity<>(stateAfterTransformation);
        }

        public Uni<T> commit() {
            return transactionState
                    .flatMap(objects -> commitTransaction(objects.getItem1(), objects.getItem2()));
        }

        public Uni<T> commitTransaction(MongoSession mongoSession, T entity) {
            return Uni.createFrom().publisher(mongoSession.commitTransaction())
                    .onFailure(retryAfter())
                    .retry()
                    .atMost(10)
                    .onFailure()
                    .invoke(throwable -> logger.error(throwable.getMessage(), throwable))
                    .onItem()
                    .transform(unused -> entity)
                    .eventually(mongoSession::close);
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
}
