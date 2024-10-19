package org.kodelabs.book;

import io.smallrye.mutiny.Uni;
import org.kodelabs.book.models.BookInfoForGroupingByAuthorNationality;
import org.kodelabs.book.models.BooksByGenre;
import org.kodelabs.book.models.BooksByPublisher;
import org.kodelabs.borrow.BookInfoAndCount;
import org.kodelabs.borrow.State;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.review.CreateReview;
import org.kodelabs.review.Review;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    BookService bookService;

    @POST
    public Uni<Book> add(CreateBook createBook) {
        return bookService.add(createBook);
    }

    @POST
    @Path("/{id}/authors/{authorId}")
    public Uni<Book> addBookToAuthor(@PathParam("id") String id, @PathParam("authorId") String authorId) {
        return bookService.addBookToAuthor(id, authorId);
    }

    @POST
    @Path("/{id}/review")
    public Uni<Review> addReviewToBook(@PathParam("id") String id, CreateReview createReview) {
        return bookService.addReviewToBook(id, createReview);
    }

    @GET
    public Uni<PageModel<Book>> getList(@BeanParam BookQueryParameters bookQueryParameters, @BeanParam PaginationQuery paginationQuery) {
        return bookService.getList(bookQueryParameters, paginationQuery);
    }

    @GET
    @Path("/{id}")
    public Uni<Book> getOneById(@PathParam("id") String id) {
        return bookService.findOneById(id);
    }

    @GET
    @Path("/group-by-publisher")
    public Uni<List<BooksByPublisher>> getBooksGroupedByPublisher() {
        return bookService.getBooksGroupedByPublisher();
    }

    @GET
    @Path("/group-by-genre")
    public Uni<List<BooksByGenre>> getBooksGroupedByGenre() {
        return bookService.getBooksGroupedByGenre();
    }

    @GET
    @Path("/group-by-author-nationality/{nationality}")
    public Uni<List<BookInfoForGroupingByAuthorNationality>> getListByAuthorsNationality(@PathParam("nationality") String nationality) {
        return bookService.getBooksGroupedByAuthorNationality(nationality);
    }

    @GET
    @Path("/suggestions")
    public Uni<List<Book>> getListOfSuggestedBooks(@QueryParam("userId") String userId) {
        return bookService.getListOfSuggestedBooks(userId);
    }

    @GET
    @Path("/borrows")
    public Uni<List<Book>> getBorrowedBooksOfUser(@QueryParam("userId") String userId,
                                                  @QueryParam("adminId") String adminId,
                                                  @QueryParam("state") State state) {
        return bookService.getListOfBorrowedBooksOfUser(userId, adminId, state);
    }

    @GET
    @Path("/most-read-in-the-last-month")
    public Uni<List<BookInfoAndCount>> getTheMostReadBooksInTheLastMonth() {
        return bookService.getTheMostReadBooksInTheLastMonth();
    }

    @PUT
    @Path("/{id}")
    public Uni<Book> update(@PathParam("id") String id, UpdateBook updateBook) {
        return bookService.updateOne(id, updateBook);
    }

    @DELETE
    @Path("/{id}")
    public Uni<CustomResponse> delete(@PathParam("id") String id) {
        return bookService.deleteOne(id);
    }

    @DELETE
    @Path("/{id}/authors/{authorId}")
    public Uni<CustomResponse> deleteAuthorFromBook(@PathParam("id") String id, @PathParam("authorId") String authorId) {
        return bookService.deleteAuthorFromBook(id, authorId);
    }
}
