package org.kodelabs.review;

import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kodelabs.book.models.AverageRatingForBook;
import org.kodelabs.book.models.CommentsForBook;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.users.UpdateUser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class ReviewRepository {

    @Inject
    MongoDb mongoDb;

    public ReactiveMongoCollection<Review> getCollection() {
        return mongoDb.getCollection("reviews", Review.class);
    }

    public Uni<Review> addOne(ClientSession clientSession, Review review) {
        review.generateId();
        review.createdAt = new Date();
        return getCollection().insertOne(clientSession, review).map(insertOneResult -> review);
    }

    public Uni<Review> addOne(Review review) {
        review.generateId();
        review.createdAt = new Date();
        return getCollection().insertOne(review).map(insertOneResult -> review);
    }

    public Uni<AverageRatingForBook> getAverageRatingForBook(ClientSession clientSession, String bookId) {
        return getCollection().aggregate(
                clientSession,
                Arrays.asList(new Document("$match",
                                new Document(Review.FIELD_BOOK_ID, bookId)),
                        new Document("$group",
                                new Document("_id", "$book.title")
                                        .append("averageRating",
                                                new Document("$push", "$averageRating"))),
                        new Document("$project",
                                new Document("averageRating",
                                        new Document("$avg", "$averageRating")))),
                AverageRatingForBook.class
        ).collect().first();
    }

    public Uni<CommentsForBook> getCommentsForBook(ClientSession clientSession, String bookId) {
        return getCollection().aggregate(
                clientSession,
                Arrays.asList(new Document("$match",
                                new Document("book._id", bookId)),
                        new Document("$sort",
                                new Document("createdAt", -1L)),
                        new Document("$limit", 5L),
                        new Document("$group",
                                new Document("_id", "$book._id")
                                        .append("comments",
                                                new Document("$push",
                                                        new Document("comment", "$comment")
                                                                .append("userInfo", "$user"))))),
                CommentsForBook.class
        ).collect().first();
    }

    public Uni<UpdateResult> updateUserReference(ClientSession clientSession, String userId, UpdateUser updateUser) {
        Bson filter = eq(Review.FIELD_USER_ID, userId);
        Bson updates = Updates.combine(
                Updates.set(Review.FIELD_USER_FIRSTNAME, updateUser.getFirstName()),
                Updates.set(Review.FIELD_USER_LASTNAME, updateUser.getLastName())
        );
        return getCollection().updateMany(clientSession, filter, updates);
    }

}
