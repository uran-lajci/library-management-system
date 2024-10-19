package org.kodelabs.book;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import org.graalvm.collections.Pair;
import org.kodelabs.author.Author;
import org.kodelabs.author.AuthorInfo;
import org.kodelabs.author.AuthorService;
import org.kodelabs.book.models.*;
import org.kodelabs.borrow.*;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.mongoDb.transactions.MongoSession;
import org.kodelabs.mongoDb.transactions.TransactionManager;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.publisher.Publisher;
import org.kodelabs.publisher.PublisherInfo;
import org.kodelabs.publisher.PublisherService;
import org.kodelabs.request.books.RequestService;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.review.CreateReview;
import org.kodelabs.review.Review;
import org.kodelabs.review.ReviewService;
import org.kodelabs.users.Role;
import org.kodelabs.users.UserService;
import org.kodelabs.validation.ValidationMethods;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class BookService {

    //region MAPPERS

    public Function<Book, BookInfo> mapToBookInfo = book ->
            new BookInfo(book._id, book.getTitle());

    //endregion
    @Inject
    ValidationMethods validationMethods;

    @Inject
    TransactionManager transactionManager;

    @Inject
    PublisherService publisherService;
    @Inject
    ReviewService reviewService;
    @Inject
    Validator validator;
    @Inject
    UserService userService;
    @Inject
    BorrowService borrowService;
    @Inject
    AuthorService authorService;
    @Inject
    BookRepository bookRepository;
    @Inject
    RequestService requestService;

    public Function<Book, Book> mapToBookFromUpdateBook(UpdateBook updateBook) {
        return book -> {
            book.setTitle(updateBook.getTitle());
            book.setDatePublished(updateBook.getDatePublished());
            book.setNumberOfBooksAvailable(updateBook.getNumberOfBooksAvailable());
            book.setNumberOfPages(updateBook.getNumberOfPages());
            book.setGenres(updateBook.getGenres());
            book.setLanguages(updateBook.getLanguages());
            return book;
        };
    }

    //region ADDERS

    public Uni<Book> add(CreateBook createBook) {
        return validator.entityValidation(createBook)
                .flatMap(validatedCreateBook -> publisherService.findOneById(createBook.getPublisherId())
                        .onFailure().transform(throwable -> new BadRequestException("Wrong publisher id")))
                .map(publisher -> publisherService.mapToPublisherInfo.apply(publisher))
                .flatMap(publisherInfo -> authorService.findManyByIds(createBook.getAuthors())
                        .flatMap(authors -> validateAuthorId(authors, createBook))
                        .map(authors -> Book.mapToBook(publisherInfo, createBook, AuthorInfo.mapToListOfAuthorInfo(authors))))
                .flatMap(book -> transactionManager.startTransaction()
                        .appendOperation(objects -> bookRepository.add(objects.getItem1().unWrap(), book))
                        .appendOperation(objects -> authorService.addBookToAuthors(objects.getItem1().unWrap(), createBook.getAuthors(), objects.getItem2()))
                        .appendOperation(objects -> publisherService.incrementNumberOfBooksPublished(objects.getItem1().unWrap(), createBook.getPublisherId()))
                        .commit().map(publisher -> book));
    }

    public Uni<Book> addBookToAuthor(String id, String authorId) {
        return findOneById(id)
                .onFailure().transform(throwable -> new BadRequestException("This book id " + id + "  is wrong"))
                .flatMap(book -> authorService.findOneById(authorId)
                        .onFailure().transform(throwable -> new BadRequestException("This author id " + id + " is wrong")))
                .flatMap(author -> transactionManager.startTransaction()
                        .appendOperation(objects -> bookRepository.addAuthorToBook(objects.getItem1().unWrap(), id, author)
                                .onItem().ifNull().failWith(new BadRequestException("Failed to add author to book")))
                        .appendOperation(objects -> authorService.addBookToAuthorWithClientSession(objects.getItem1().unWrap(), authorId, objects.getItem2())
                                .map(addedAuthor -> objects.getItem2()))
                        .commit());
    }

    public Uni<Review> addReviewToBook(String id, CreateReview createReview) {
        return reviewService.add(id, createReview);
    }
    //endregion

    //region GETTERS

    public Uni<PageModel<Book>> getList(BookQueryParameters bookQueryParameters, PaginationQuery paginationQuery) {
        return bookRepository.getList(bookQueryParameters, paginationQuery);
    }

    public Uni<List<BooksByPublisher>> getBooksGroupedByPublisher() {
        return bookRepository.getBooksGroupedByPublisher();
    }

    public Uni<List<BooksByGenre>> getBooksGroupedByGenre() {
        return bookRepository.getBooksGroupedByGender();
    }

    public Uni<List<BookInfoForGroupingByAuthorNationality>> getBooksGroupedByAuthorNationality(String nationality) {
        return bookRepository.getBooksGroupedByAuthorNationality(nationality);
    }

    public Uni<Book> findOneById(String id) {
        return bookRepository.findOneById(id)
                .onItem().ifNull().failWith(() -> new BaseException(Response.Status.NOT_FOUND));
    }

    public Uni<List<String>> findBookIdsByPublisherId(String publisherId) {
        return bookRepository.findManyByPublisherId(publisherId)
                .map(books -> books.stream().map(book -> book._id).collect(Collectors.toList()));
    }

    public Uni<GenresOfBooks> findGenresByIds(List<String> ids) {
        return bookRepository.findGenresByIds(ids)
                .onItem().ifNull().failWith(new BaseException(Response.Status.NOT_FOUND));
    }

    public Uni<List<Book>> getListOfSuggestedBooks(String userId) {
        return requestService.getIdsOfBooksOfUser(userId)
                .onFailure().transform(throwable -> new BadRequestException("Failed"))
                .flatMap(requestedBooksForUser -> findGenresByIds(requestedBooksForUser.getBooks())
                        .map(genresOfBooks -> Pair.create(requestedBooksForUser, genresOfBooks)))
                .flatMap(pair -> bookRepository.getListOfSuggestionsBasedOnGenres(pair.getLeft().getBooks(), pair.getRight().getGenres()));
    }

    public Uni<List<Book>> getListOfBorrowedBooksOfUser(String userId, String adminId, State state) {
        return userService.findOneByIdAndRole(adminId, Role.ADMIN)
                .onFailure().transform(throwable -> new BadRequestException("There is no internal user with the given id "))
                .flatMap(user -> userService.findOneByIdAndRole(userId, Role.USER)
                        .onFailure().transform(throwable -> new BadRequestException("There is no external user with the given id")))
                .flatMap(user -> borrowService.getIdsOfBooksOfUser(userId, state)
                        .flatMap(idsOfBorrowedBooksOfUser -> getBorrowedBooksOfUser(idsOfBorrowedBooksOfUser, state)));
    }

    public Uni<List<Book>> getBorrowedBooksOfUser(IdsOfBorrowedBooksOfUser idsOfBorrowedBooksOfUser, State state) {
        if (idsOfBorrowedBooksOfUser.getIds() == null && state.equals(State.RETURNED)) {
            return Uni.createFrom().failure(new BadRequestException("The user has no books returned"));
        } else if (idsOfBorrowedBooksOfUser.getIds() == null && state.equals(State.BORROWED)) {
            return Uni.createFrom().failure(new BadRequestException("The user has no books borrowed"));
        } else {
            return bookRepository.findManyByIds(idsOfBorrowedBooksOfUser.getIds());
        }
    }

    public Uni<List<BookInfoAndCount>> getTheMostReadBooksInTheLastMonth() {
        return borrowService.getTheMostReadBooksInTheLastMonth();
    }

    //endregion

    //region UPDATES

    public Uni<Book> updateOne(String id, UpdateBook updateBook) {
        return validator.entityValidation(updateBook)
                .flatMap(book -> findOneById(id)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id for book")))
                .map(mapToBookFromUpdateBook(updateBook))
                .flatMap(book -> {
                    PublisherInfo oldPublisher = book.getPublisher();

                    return publisherService.findOneById(updateBook.getPublisherId())
                            .onFailure().transform(throwable -> new BadRequestException("Wrong id for publisher"))
                            .map(updatePublisher -> {
                                book.setPublisher(new PublisherInfo(updatePublisher._id, updatePublisher.getName()));
                                return updatePublisher;
                            }).flatMap(updatedPublisher -> transactionManager.startTransaction()
                                    .appendOperation(mongodbSessionVoid -> bookRepository.update(mongodbSessionVoid.getItem1().unWrap(), id, book))
                                    .appendOperation(mongodbSessionBook -> updatePublisher(mongodbSessionBook, oldPublisher, updatedPublisher))
                                    .appendOperation(mongodbSessionBook -> authorService.updateBookReference(mongodbSessionBook.getItem1().unWrap(), id, updateBook)
                                            .map(ignore -> mongodbSessionBook.getItem2()))
                                    .commit());
                });
    }

    public Uni<Book> updatePublisher(Tuple2<MongoSession, Book> mongodbSessionBook, PublisherInfo oldPublisher, Publisher updatePublisher) {
        if (oldPublisher.getId().equals(updatePublisher._id)) {
            return Uni.createFrom().item(mongodbSessionBook.getItem2());
        } else {
            return publisherService.updateNumberOfBooks(mongodbSessionBook.getItem1().unWrap(), updatePublisher._id, oldPublisher.getId())
                    .flatMap(ignore -> Uni.createFrom().item(mongodbSessionBook.getItem2()));
        }
    }

    public Uni<UpdateResult> updateAuthorReference(ClientSession clientSession, String id, Author author) {
        return bookRepository.updateAuthorReference(clientSession, id, author);
    }

    public Uni<Book> updateAverageRatingAndComments(ClientSession clientSession, String id, double averageRating, List<CommentAndUserInfo> comments) {
        return bookRepository.updateAverageRatingAndComments(clientSession, id, averageRating, comments);
    }

    public Uni<org.kodelabs.mongoDb.entities.UpdateResult> updatePublisherReference(MongoSession mongoSession, String publisherId, String publisherName) {
        return bookRepository.updatePublisherReference(mongoSession, publisherId, publisherName);
    }

    //region DELETES

    public Uni<CustomResponse> deleteOne(String id) {
        return findOneById(id)
                .onFailure().transform(throwable -> new BadRequestException("Failure"))
                .flatMap(book -> transactionManager.startTransaction()
                        .appendOperation(objects -> bookRepository.deleteOne(objects.getItem1().unWrap(), id))
                        .appendOperation(objects -> authorService.deleteBookFromAuthors(objects.getItem1().unWrap(), book._id))
                        .appendOperation(objects -> requestService.deleteOne(objects.getItem1().unWrap(), id))
                        .appendOperation(objects -> publisherService.decrementNumberOfBooksPublished(objects.getItem1().unWrap(), book.getPublisher().getId()))
                        .commit().map(request -> new CustomResponse(Response.Status.OK, "Success")));
    }

    public Uni<CustomResponse> deleteAuthorFromBook(String id, String authorId) {
        return bookRepository.removeAuthorFromBook(id, authorId)
                .flatMap(validationMethods.checkForNull())
                .flatMap(response -> authorService.deleteBookFromAuthor(authorId, id));
    }

    public Uni<Book> incrementNumberOfBooksAvailable(ClientSession clientSession, String id) {
        return bookRepository.incrementNumberOfBooksAvailable(clientSession, id);
    }

    public Uni<Book> decrementNumberOfBooksAvailable(ClientSession clientSession, String id) {
        return bookRepository.decrementNumberOfBooksAvailable(clientSession, id);
    }
    //endregion

    //region VALIDATES

    public Uni<List<Author>> validateAuthorId(List<Author> authors, CreateBook createBook) {
        if (authors.size() != createBook.getAuthors().size()) {
            return Uni.createFrom().failure(new BadRequestException("authorId is not found"));
        } else {
            return Uni.createFrom().item(authors);
        }
    }
    //endregion
}
