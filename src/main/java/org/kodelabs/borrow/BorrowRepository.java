package org.kodelabs.borrow;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.request.books.Request;
import org.kodelabs.users.UpdateUser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class BorrowRepository {
    @Inject
    MongoDb mongoDb;

    private ReactiveMongoCollection<Borrow> getCollection() {
        return mongoDb.getCollection("borrows", Borrow.class);
    }

    public Uni<Borrow> addOne(ClientSession clientSession, Borrow borrow) {
        borrow.generateId();
        return getCollection().insertOne(clientSession, borrow).map(insertOneResult -> borrow);
    }

    public Uni<Borrow> findBorrowedBookFromUser(String id, String userId) {
        Bson filter = and(
                eq(Borrow.FIELD_ID, id),
                eq(Borrow.FIELD_USER_ID, userId)
        );
        return getCollection().find(filter).collect().first();
    }

    public Uni<Borrow> findBorrowFromBookAndUser(String bookId, String userId) {
        Bson filter = and(
                eq(Borrow.FIELD_USER_ID, userId),
                eq(Borrow.FIELD_BOOK_ID, bookId)
        );
        return getCollection().find(filter).collect().first();
    }

    public Uni<IdsOfBorrowedBooksOfUser> getIdsOfBorrowedBooksOfUser(String userId, State state) {
        return getCollection().aggregate(
                Arrays.asList(new Document("$match",
                                new Document(Borrow.FIELD_USER_ID, userId)
                                        .append(Borrow.FIELD_STATE, state.name())),
                        new Document("$project",
                                new Document(Borrow.FIELD_BOOK_ID, 1L)),
                        new Document("$group",
                                new Document("_id", Borrow.FIELD_BOOK_ID)
                                        .append("ids",
                                                new Document("$push", "$book._id")))),
                IdsOfBorrowedBooksOfUser.class
        ).collect().first();
    }

    public Uni<List<BookInfoAndCount>> getTheMostReadBooksInTheLastMonth() {
        Date date = Date.from(ZonedDateTime.now().minusMonths(1).toInstant());

        return getCollection().aggregate(
                Arrays.asList(new Document("$lookup",
                                new Document("from", "books")
                                        .append("localField", "book._id")
                                        .append("foreignField", "_id")
                                        .append("as", "books")),
                        new Document("$match",
                                new Document("endDate",
                                        new Document("$gt", date))),
                        new Document("$project",
                                new Document("bookId", "$book._id")
                                        .append("title", "$book.title")
                                        .append("languages",
                                                new Document("$reduce",
                                                        new Document("input", "$books.languages")
                                                                .append("initialValue", List.of())
                                                                .append("in",
                                                                        new Document("$setUnion", Arrays.asList("$$value", "$$this")))))
                                        .append("genres",
                                                new Document("$reduce",
                                                        new Document("input", "$books.genres")
                                                                .append("initialValue", List.of())
                                                                .append("in",
                                                                        new Document("$setUnion", Arrays.asList("$$value", "$$this")))))),
                        new Document("$group",
                                new Document("_id", "$bookId")
                                        .append("title",
                                                new Document("$first", "$title"))
                                        .append("genres",
                                                new Document("$first", "$genres"))
                                        .append("languages",
                                                new Document("$first", "$languages"))
                                        .append("count",
                                                new Document("$sum", 1L))),
                        new Document("$sort",
                                new Document("count", -1L)),
                        new Document("$limit", 5L)),
                BookInfoAndCount.class
        ).collect().asList();
    }

    public Uni<Borrow> updateState(ClientSession clientSession, String id, State state) {
        Bson filter = eq(Borrow.FIELD_ID, id);
        Bson update = set(Borrow.FIELD_STATE, state.name());
        return getCollection().findOneAndUpdate(clientSession, filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<UpdateResult> updateUserReference(ClientSession clientSession, String userId, UpdateUser updateUser) {
        Bson filer = eq(Request.FIELD_USER_ID, userId);
        Bson updates = combine(
                set(Request.FIELD_USER_FIRSTNAME, updateUser.getFirstName()),
                set(Request.FIELD_USER_LASTNAME, updateUser.getLastName())
        );
        return getCollection().updateMany(clientSession, filer, updates);
    }

}
