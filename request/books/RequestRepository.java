package org.kodelabs.request.books;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kodelabs.borrow.Borrow;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.request.books.models.IdsOfRequestedBooksFromUser;
import org.kodelabs.users.UpdateUser;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

@ApplicationScoped
public class RequestRepository {
    @Inject
    MongoDb mongoDb;

    public ReactiveMongoCollection<Request> getCollection() {
        return mongoDb.getCollection("requests", Request.class);
    }

    public Uni<Request> addOne(Request request) {
        request.generateId();
        return getCollection().insertOne(request).map(insertOneResult -> request);
    }

    public Uni<Request> findOneById(String id) {
        return getCollection().find(eq(Request.FIELD_ID, id)).collect().first();
    }

    public Uni<Request> findRequestWithSpecifiedUserId(String id, String userId) {
        Bson filter = and(
                eq(Request.FIELD_ID, id),
                eq(Request.FIELD_USER_ID, userId));
        return getCollection().find(filter).collect().first();
    }

    public Uni<PageModel<Request>> getList(RequestQueryParameters requestQueryParameters, PaginationQuery paginationQuery) {
        return PageModel.mapToPageModel(getCollection(), requestQueryParameters.toBson(), paginationQuery);
    }

    public Uni<IdsOfRequestedBooksFromUser> getIdsOfBooksOfUser(String userId) {
        return getCollection().aggregate(
                Arrays.asList(new Document("$match",
                                new Document(Request.FIELD_USER_ID, userId)),
                        new Document("$project",
                                new Document(Request.FIELD_BOOK_ID, 1L)
                                        .append(Request.FIELD_USER_ID, 1L)),
                        new Document("$group",
                                new Document("_id", "$user._id")
                                        .append("books",
                                                new Document("$push", "$book._id")))),
                IdsOfRequestedBooksFromUser.class).collect().first();
    }

    public Uni<List<Request>> getRequestsOfBookInStateApprovedInTheSpecifiedTime(String bookId, LocalDate startDate, LocalDate endDate) {
        Bson filter = and(
                eq(Request.FIELD_BOOK_ID, bookId),
                eq(Request.FIELD_STATUS, Status.APPROVED.name()),
                gt(Request.FIELD_END_DATE, startDate),
                lt(Request.FIELD_START_DATE, endDate)
        );
        return getCollection().find(filter).collect().asList();
    }

    public Uni<Long> countPendingRequestsOfUser(String userId) {
        Bson filter = and(eq(Request.FIELD_USER_ID, userId), eq(Request.FIELD_STATUS, Status.PENDING.name()));
        return getCollection().countDocuments(filter);
    }

    public Uni<Request> checkIfUserHasReservedBook(Borrow borrow) {
        Bson filter = and(
                eq(Request.FIELD_BOOK_ID, borrow.getBook().getId()),
                eq(Request.FIELD_USER_ID, borrow.getUser().getId()),
                eq(Request.FIELD_STATUS, Status.APPROVED.name()),
                eq(Request.FIELD_START_DATE, borrow.getStartDate()),
                eq(Request.FIELD_END_DATE, borrow.getEndDate())
        );

        return getCollection().find(filter).collect().first();
    }

    public Uni<Request> checkBookAvailabilityInTimeline(String bookId, LocalDate startDate, LocalDate endDate) {
        Bson filter = and(
                eq(Request.FIELD_BOOK_ID, bookId),
                eq(Request.FIELD_STATUS, Status.APPROVED.name()),
                gt(Request.FIELD_END_DATE, startDate),
                lt(Request.FIELD_START_DATE, endDate));
        return getCollection().find(filter).collect().first();
    }

    public Uni<Request> updateStatus(Request request) {
        return getCollection().replaceOne(eq(Request.FIELD_ID, request._id), request).map(updateResult -> request);
    }

    public Uni<Request> update(Request request, Status approvedOrRejected) {
        Bson filter = eq(Request.FIELD_ID, request._id);
        Bson update = combine(
                set(Request.FIELD_START_DATE, request.getStartDate()),
                set(Request.FIELD_END_DATE, request.getEndDate()),
                set(Request.FIELD_STATUS, approvedOrRejected.name()),
                set(Request.FIELD_BOOK, request.getBook()),
                set(Request.FIELD_USER, request.getUser())
        );
        return getCollection().findOneAndUpdate(filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<UpdateResult> updateUserReference(ClientSession clientSession, String userId, UpdateUser updateUser) {
        Bson filer = eq(Request.FIELD_USER_ID, userId);
        Bson updates = combine(
                set(Request.FIELD_USER_FIRSTNAME, updateUser.getFirstName()),
                set(Request.FIELD_USER_LASTNAME, updateUser.getLastName())
        );
        return getCollection().updateMany(clientSession, filer, updates);
    }

    public Uni<Request> deleteOne(ClientSession clientSession, String bookId) {
        return getCollection().findOneAndDelete(clientSession, eq(Request.FIELD_BOOK_ID, bookId));
    }

}
