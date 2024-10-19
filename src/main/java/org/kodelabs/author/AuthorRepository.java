package org.kodelabs.author;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.conversions.Bson;
import org.kodelabs.book.Book;
import org.kodelabs.book.UpdateBook;
import org.kodelabs.book.models.BookInfo;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.elemMatch;
import static com.mongodb.client.model.Updates.push;

@ApplicationScoped
public class AuthorRepository {

    @Inject
    MongoDb mongoDb;

    public ReactiveMongoCollection<Author> getCollection() {
        return mongoDb.getCollection("authors", Author.class);
    }

    public Uni<Author> addOne(Author author) {
        author.generateId();
        return getCollection().insertOne(author).map(insertOneResult -> author);
    }

    public Uni<UpdateResult> addBookToAuthors(ClientSession clientSession, Set<String> ids, Book book) {
        Bson filter = in(Author.FIELD_ID, ids);
        Bson append = push(Author.FIELD_BOOKS_WRITTEN, new BookInfo(book._id, book.getTitle()));
        return getCollection().updateMany(clientSession, filter, append);
    }

    public Uni<Author> addBookToAuthor(ClientSession clientSession, String authorId, Book book) {
        Bson filter = and(eq(Author.FIELD_ID, authorId), nin(Author.FIELD_BOOKS_WRITTEN_ID, book._id));
        Bson insert = Updates.push(Author.FIELD_BOOKS_WRITTEN, new BookInfo(book._id, book.getTitle()));
        return getCollection().findOneAndUpdate(clientSession, filter, insert, new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
    }

    public Uni<PageModel<Author>> getList(AuthorQueryParameters authorQueryParameters, PaginationQuery paginationQuery) {
        return PageModel.mapToPageModel(getCollection(), authorQueryParameters.toBson(), paginationQuery);
    }

    public Uni<Author> findOneById(String id) {
        return getCollection().find(eq(Author.FIELD_ID, id)).collect().first();
    }

    public Uni<List<Author>> findManyByIds(Set<String> ids) {
        return getCollection().find(in(Author.FIELD_ID, ids)).collect().asList();
    }

    public Uni<Author> update(ClientSession clientSession, String id, Author author) {
        return getCollection().replaceOne(clientSession, eq(Author.FIELD_ID, id), author).map(updateResult -> author);
    }

    public Uni<UpdateResult> updateBookReference(ClientSession clientSession, String id, UpdateBook updateBook) {
        Bson filter = elemMatch(Author.FIELD_BOOKS_WRITTEN, eq(Book.FIELD_ID, id));
        Bson set = Updates.set("booksWritten.$.title", updateBook.getTitle());
        return getCollection().updateMany(clientSession, filter, set);
    }

    public Uni<UpdateResult> deleteBookFromBooksWritten(ClientSession clientSession, String bookId) {
        Bson filter = elemMatch(Author.FIELD_BOOKS_WRITTEN, eq(Book.FIELD_ID, bookId));
        Bson delete = Updates.pull(Author.FIELD_BOOKS_WRITTEN, eq(Book.FIELD_ID, bookId));
        return getCollection().updateMany(clientSession, filter, delete);
    }

    public Uni<Author> deleteBookFromAuthor(String id, String bookId) {
        Bson filter = and(
                eq(Author.FIELD_ID, id),
                eq("booksWritten._id", bookId)
        );
        Bson delete = Updates.pull(Author.FIELD_BOOKS_WRITTEN, eq(Book.FIELD_ID, bookId));
        return getCollection().findOneAndUpdate(filter, delete);
    }

    public Uni<Author> delete(String id) {
        Bson filter = eq(Author.FIELD_ID, id);
        Bson exists = exists("booksWritten.0", false);
        return getCollection().findOneAndDelete(and(filter, exists));
    }

}
