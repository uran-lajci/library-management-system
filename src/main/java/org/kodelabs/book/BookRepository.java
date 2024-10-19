package org.kodelabs.book;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.kodelabs.author.Author;
import org.kodelabs.author.AuthorInfo;
import org.kodelabs.book.models.*;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.mongoDb.transactions.MongoSession;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.elemMatch;
import static com.mongodb.client.model.Updates.*;
import static org.kodelabs.mongoDb.mutiny.Db.updateMany;

@ApplicationScoped
public class BookRepository {

    @Inject
    MongoDb mongoDb;

    private ReactiveMongoCollection<Book> getCollection() {
        return mongoDb.getCollection("books", Book.class);
    }

    public Uni<Book> add(ClientSession clientSession, Book book) {
        book.generateId();
        return getCollection().insertOne(clientSession, book).map(insertOneResult -> book);
    }

    public Uni<Book> addAuthorToBook(ClientSession clientSession, String id, Author author) {
        Bson filter = eq(Book.FIELD_ID, id);
        Bson add = push(Book.FIELD_AUTHORS, new AuthorInfo(author._id, author.getFirstName(), author.getLastName()));
        return getCollection().findOneAndUpdate(clientSession, filter, add, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<PageModel<Book>> getList(BookQueryParameters bookQueryParameters, PaginationQuery paginationQuery) {
        return PageModel.mapToPageModel(getCollection(), bookQueryParameters.toBson(), paginationQuery);
    }

    public Uni<List<BooksByPublisher>> getBooksGroupedByPublisher() {
        return getCollection().aggregate(
                List.of(new Document("$group",
                        new Document("_id", "$" + Book.FIELD_PUBLISHER_ID)
                                .append(Book.FIELD_PUBLISHER,
                                        new Document("$first", "$" + Book.FIELD_PUBLISHER_NAME))
                                .append("books",
                                        new Document("$push",
                                                new Document(Book.FIELD_ID, "$" + Book.FIELD_ID)
                                                        .append(Book.FIELD_TITLE, "$" + Book.FIELD_TITLE)
                                                        .append(Book.FIELD_AUTHORS, "$" + Book.FIELD_AUTHORS)
                                                        .append(Book.FIELD_NUMBER_OF_PAGES, "$" + Book.FIELD_NUMBER_OF_PAGES)
                                                        .append(Book.FIELD_DATE_PUBLISHED, "$" + Book.FIELD_DATE_PUBLISHED))))),
                BooksByPublisher.class).collect().asList();
    }

    public Uni<List<BooksByGenre>> getBooksGroupedByGender() {
        return getCollection().aggregate(
                Arrays.asList(new Document("$unwind",
                                new Document("path", "$genres")),
                        new Document("$group",
                                new Document("_id", "$genres")
                                        .append(Book.FIELD_GENRES,
                                                new Document("$first", "$genres"))
                                        .append("books",
                                                new Document("$push",
                                                        new Document(Book.FIELD_ID, "$" + Book.FIELD_ID)
                                                                .append(Book.FIELD_TITLE, "$" + Book.FIELD_TITLE)
                                                                .append(Book.FIELD_AUTHORS, "$" + Book.FIELD_AUTHORS)
                                                                .append(Book.FIELD_NUMBER_OF_PAGES, "$" + Book.FIELD_NUMBER_OF_PAGES)
                                                                .append(Book.FIELD_DATE_PUBLISHED, "$datePublished"))))),
                BooksByGenre.class).collect().asList();
    }

    public Uni<List<BookInfoForGroupingByAuthorNationality>> getBooksGroupedByAuthorNationality(String authorNationality) {
        return getCollection().aggregate(
                Arrays.asList(new Document("$lookup",
                                new Document("from", "authors")
                                        .append("localField", Book.FIELD_AUTHORS_AUTHOR_ID)
                                        .append("foreignField", Book.FIELD_ID)
                                        .append("as", BookInfoForGroupingByAuthorNationality.FIELD_AUTHOR_INFO)),
                        new Document("$match",
                                new Document(BookInfoForGroupingByAuthorNationality.FIELD_AUTHOR_INFO_NATIONALITY, authorNationality)),
                        new Document("$project",
                                new Document(BookInfoForGroupingByAuthorNationality.FIELD_TITLE, 1L)
                                        .append(BookInfoForGroupingByAuthorNationality.FIELD_AUTHOR_INFO_ID, 1L)
                                        .append(BookInfoForGroupingByAuthorNationality.FIELD_AUTHOR_INFO_FIRSTNAME, 1L)
                                        .append(BookInfoForGroupingByAuthorNationality.FIELD_AUTHOR_INFO_LASTNAME, 1L)
                                        .append(BookInfoForGroupingByAuthorNationality.FIELD_AUTHOR_INFO_NATIONALITY, 1L))),
                BookInfoForGroupingByAuthorNationality.class).collect().asList();
    }

    public Uni<Book> findOneById(String id) {
        return getCollection().find(eq(Book.FIELD_ID, id)).collect().first();
    }

    public Uni<List<Book>> findManyByPublisherId(String publisherId) {
        Bson filter = eq(Book.FIELD_PUBLISHER_ID, publisherId);
        return getCollection().find(filter).collect().asList();
    }

    public Uni<List<Book>> findManyByIds(List<String> ids) {
        return getCollection().find(in(Book.FIELD_ID, ids)).collect().asList();
    }

    public Uni<GenresOfBooks> findGenresByIds(List<String> ids) {
        return getCollection().aggregate(Arrays.asList(new Document("$match",
                                new Document("_id",
                                        new Document("$in", ids))),
                        new Document("$group",
                                new Document("_id", "genres")
                                        .append("genres",
                                                new Document("$addToSet", "$genres"))),
                        new Document("$project",
                                new Document("genres",
                                        new Document("$reduce",
                                                new Document("input", "$genres")
                                                        .append("initialValue", List.of())
                                                        .append("in",
                                                                new Document("$concatArrays", Arrays.asList("$$this", "$$value"))))))),
                GenresOfBooks.class).collect().first();
    }

    public Uni<List<Book>> getListOfSuggestionsBasedOnGenres(List<String> ids, List<String> genres) {
        Bson filter = and(
                nin(Book.FIELD_ID, ids),
                in(Book.FIELD_GENRES, genres)
        );
        return getCollection().find(filter).collect().asList();
    }

    public Uni<Book> update(ClientSession clientSession, String id, Book book) {
        Bson filter = eq(Book.FIELD_ID, id);
        return getCollection().replaceOne(clientSession, filter, book).map(updateResult -> book);
    }

    public Uni<UpdateResult> updateAuthorReference(ClientSession clientSession, String id, Author author) {
        Bson filter = Filters.elemMatch(Book.FIELD_AUTHORS, Filters.eq(Author.FIELD_ID, id));
        Bson set = Updates.combine(
                Updates.set("authors.$.firstName", author.getFirstName()),
                Updates.set("authors.$.lastName", author.getLastName())
        );
        return getCollection().updateMany(clientSession, filter, set);
    }

    public Uni<Book> updateAverageRatingAndComments(ClientSession clientSession, String id, double averageRating, List<CommentAndUserInfo> comments) {
        Bson filter = eq(Book.FIELD_ID, id);
        Bson update = Updates.combine(
                set(Book.FIELD_RATING, averageRating),
                set(Book.FIELD_COMMENTS, comments)
        );
        return getCollection().findOneAndUpdate(clientSession, filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<org.kodelabs.mongoDb.entities.UpdateResult> updatePublisherReference(MongoSession mongoSession, String publisherId, String publisherName) {
        return updateMany(getCollection(), mongoSession,
                eq(Book.FIELD_PUBLISHER_ID, publisherId),
                set(Book.FIELD_PUBLISHER_NAME, publisherName));
    }

    public Uni<Book> deleteOne(ClientSession clientSession, String id) {
        return getCollection().findOneAndDelete(clientSession, eq(Book.FIELD_ID, id));
    }

    public Uni<Book> removeAuthorFromBook(String id, String authorId) {
        Bson filter = and(
                elemMatch(Book.FIELD_AUTHORS, eq(Author.FIELD_ID, authorId)),
                eq(Book.FIELD_ID, id),
                exists("authors.1", true));

        Bson delete = Updates.pull(Book.FIELD_AUTHORS, eq(Author.FIELD_ID, authorId));
        return getCollection().findOneAndUpdate(filter, delete, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<Book> incrementNumberOfBooksAvailable(ClientSession clientSession, String id) {
        Bson filter = eq(Book.FIELD_ID, id);
        Bson update = inc(Book.FIELD_NUMBER_OF_BOOKS_AVAILABLE, 1);
        return getCollection().findOneAndUpdate(clientSession, filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<Book> decrementNumberOfBooksAvailable(ClientSession clientSession, String id) {
        Bson filter = eq(Book.FIELD_ID, id);
        Bson update = inc(Book.FIELD_NUMBER_OF_BOOKS_AVAILABLE, -1);
        return getCollection().findOneAndUpdate(clientSession, filter, update, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }
}
