package org.kodelabs.publisher;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.mongoDb.entities.DeleteResult;
import org.kodelabs.mongoDb.mutiny.Db;
import org.kodelabs.mongoDb.transactions.MongoSession;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;
import static org.kodelabs.mongoDb.mutiny.Db.insertOne;
import static org.kodelabs.mongoDb.mutiny.Db.replaceOne;

@ApplicationScoped
public class PublisherRepository {
    @Inject
    MongoDb mongoDb;

    public ReactiveMongoCollection<Publisher> getCollection() {
        return mongoDb.getCollection("publishers", Publisher.class);
    }

    public Uni<Publisher> addOne(Publisher publisher) {
        publisher.generateId();
        return insertOne(getCollection(), publisher).map(insertOneResult -> publisher);
    }

    public Uni<Publisher> findOneById(String id) {
        return getCollection().find(eq(Publisher.FIELD_ID, id)).collect().first();
    }

    public Uni<Publisher> updateOne(MongoSession mongoSession, Publisher publisher) {
        return replaceOne(getCollection(), mongoSession, eq(Publisher.FIELD_ID, publisher._id), publisher).map(updateResult -> publisher);
    }

    public Uni<BulkWriteResult> updateNumberOfBooks(ClientSession clientSession, String idForIncrement, String idForDecrement) {
        return getCollection().bulkWrite(clientSession, Arrays.asList(
                new UpdateOneModel<>(eq(Publisher.FIELD_ID, idForIncrement), inc(Publisher.FIELD_NUMBER_OF_BOOKS, 1)),
                new UpdateOneModel<>(eq(Publisher.FIELD_ID, idForDecrement), inc(Publisher.FIELD_NUMBER_OF_BOOKS, -1))
        ));
    }

    public Uni<Publisher> incrementNumberOfBooksPublished(ClientSession clientSession, String id) {
        return getCollection().findOneAndUpdate(
                clientSession,
                eq(Publisher.FIELD_ID, id),
                inc(Publisher.FIELD_NUMBER_OF_BOOKS, 1),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<Publisher> decrementNumberOfBooksPublished(ClientSession clientSession, String id) {
        return getCollection().findOneAndUpdate(
                clientSession,
                eq(Publisher.FIELD_ID, id),
                inc(Publisher.FIELD_NUMBER_OF_BOOKS, -1),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<DeleteResult> deleteOne(String id) {
        return Db.deleteOne(getCollection(), eq(Publisher.FIELD_ID, id));
    }
}
