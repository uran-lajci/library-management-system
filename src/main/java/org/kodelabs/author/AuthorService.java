package org.kodelabs.author;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.kodelabs.book.Book;
import org.kodelabs.book.BookService;
import org.kodelabs.book.UpdateBook;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.mongoDb.transactions.TransactionManager;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.validation.ValidationMethods;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

@ApplicationScoped
public class AuthorService {

    @Inject
    TransactionManager transactionManager;

    @Inject
    ValidationMethods validationMethods;

    @Inject
    Validator validator;

    @Inject
    BookService bookService;

    @Inject
    AuthorRepository authorRepository;

    //region MAPPERS

    Function<CreateAuthor, Author> mapToAuthor = (createAuthor) ->
            new Author(createAuthor.getFirstName(), createAuthor.getLastName(), List.of(), createAuthor.getBorn(),
                    createAuthor.getDied(), createAuthor.getPlaceBorn(), createAuthor.getPlaceDied(), createAuthor.getNationality(),
                    createAuthor.getCurrentProfession());

    public Function<Author, Author> mapToAuthorFromUpdateAuthor(AuthorUpdate authorUpdate) {
        return author -> {
            author.setFirstName(authorUpdate.getFirstName());
            author.setLastName(authorUpdate.getLastName());
            author.setBorn(authorUpdate.getBorn());
            author.setDied(authorUpdate.getDied());
            author.setPlaceBorn(authorUpdate.getPlaceBorn());
            author.setPlaceDied(authorUpdate.getPlaceDied());
            author.setNationality(authorUpdate.getNationality());
            author.setCurrentProfession(authorUpdate.getCurrentProfession());
            return author;
        };
    }
    //endregion

    //region ADDERS

    public Uni<Author> add(CreateAuthor createAuthor) {
        return validator.entityValidation(createAuthor)
                .flatMap(validateAuthorBornDateBeforeDiedDate())
                .map(validatedCreateAuthor -> mapToAuthor.apply(validatedCreateAuthor))
                .flatMap(author -> authorRepository.addOne(author));
    }

    public Uni<UpdateResult> addBookToAuthors(ClientSession clientSession, Set<String> ids, Book book) {
        return authorRepository.addBookToAuthors(clientSession, ids, book);
    }

    public Uni<Author> addBookToAuthorWithClientSession(ClientSession clientSession, String id, Book book) {
        return authorRepository.addBookToAuthor(clientSession, id, book)
                .onItem().ifNull().failWith(() -> new BadRequestException("The book is in this author"));
    }

    //endregion

    //region GETTERS

    public Uni<PageModel<Author>> getList(AuthorQueryParameters authorQueryParameters, PaginationQuery paginationQuery) {
        return authorRepository.getList(authorQueryParameters, paginationQuery);
    }

    public Uni<Author> findOneById(String id) {
        return authorRepository.findOneById(id)
                .onItem().ifNull().failWith(() -> new BaseException(Response.Status.NOT_FOUND));
    }

    public Uni<List<Author>> findManyByIds(Set<String> ids) {
        return authorRepository.findManyByIds(ids);
    }
    //endregion

    //region UPDATES

    public Uni<Author> updateOne(String id, AuthorUpdate authorUpdate) {
        return validator.entityValidation(authorUpdate)
                .flatMap(validatedAuthorUpdate -> findOneById(id)
                        .onFailure().transform(throwable -> new BadRequestException("Author id is wrong")))
                .map(mapToAuthorFromUpdateAuthor(authorUpdate))
                .flatMap(author -> transactionManager.startTransaction()
                        .appendOperation(objects -> authorRepository.update(objects.getItem1().unWrap(), id, author))
                        .appendOperation(objects -> bookService.updateAuthorReference(objects.getItem1().unWrap(), id, objects.getItem2()))
                        .commit().map(updateResult -> author)
                );
    }

    public Uni<UpdateResult> updateBookReference(ClientSession clientSession, String id, UpdateBook updateBook) {
        return authorRepository.updateBookReference(clientSession, id, updateBook);
    }
    //endregion

    //region DELETES

    public Uni<CustomResponse> deleteBookFromAuthors(ClientSession clientSession, String id) {
        return authorRepository.deleteBookFromBooksWritten(clientSession, id)
                .onItem().ifNull().failWith(() -> new BadRequestException("There is no author with id " + id))
                .map(author -> new CustomResponse(Response.Status.OK, "Success"));
    }

    public Uni<CustomResponse> delete(String id) {
        return authorRepository.delete(id)
                .flatMap(validationMethods.checkForNull());
    }

    public Uni<CustomResponse> deleteBookFromAuthor(String id, String bookId) {
        return authorRepository.deleteBookFromAuthor(id, bookId)
                .onItem().ifNull().failWith(() -> new BadRequestException("There is no author with id " + id))
                .map(author -> new CustomResponse(Response.Status.OK, "Success"));
    }
    //endregion

    //region VALIDATES

    public Function<CreateAuthor, Uni<? extends CreateAuthor>> validateAuthorBornDateBeforeDiedDate() {
        return (createAuthor -> {
            if (createAuthor.getDied() != null) {
                if (createAuthor.getDied().isAfter(createAuthor.getBorn())) {
                    return Uni.createFrom().item(createAuthor);
                } else {
                    return Uni.createFrom().failure(new BadRequestException("The date of authors death should be later than the birth date"));
                }
            }
            return Uni.createFrom().item(createAuthor);
        });
    }
    //endregion
}
