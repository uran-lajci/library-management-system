package org.kodelabs.publisher;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.kodelabs.TriFunction;
import org.kodelabs.book.BookService;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.mongoDb.transactions.TransactionManager;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.users.Role;
import org.kodelabs.users.User;
import org.kodelabs.users.UserService;
import org.kodelabs.users.models.UserInfo;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class PublisherService {

    //region MAPPERS
    public TriFunction<String, UserInfo, Integer, Publisher> mapToPublisher =
            Publisher::new;

    public Function<Publisher, PublisherInfo> mapToPublisherInfo = publisher ->
            new PublisherInfo(publisher._id, publisher.getName());
    //endregion
    @Inject
    TransactionManager transactionManager;
    @Inject
    BookService bookService;
    @Inject
    UserService userService;
    @Inject
    Validator validator;
    @Inject
    PublisherRepository publisherRepository;

    public Uni<Publisher> addOne(CreatePublisher createPublisher, String userId) {
        return validator.entityValidation(createPublisher)
                .flatMap(validatedCreatePublisher -> userService.findOneById(userId)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong user id")))
                .flatMap(checkIfUserIsInternal())
                .map(user -> userService.mapToUserInfo.apply(user))
                .map(userInfo -> mapToPublisher.apply(createPublisher.getName(), userInfo, 0))
                .flatMap(publisher -> publisherRepository.addOne(publisher));
    }

    public Uni<Publisher> findOneById(String id) {
        return publisherRepository.findOneById(id)
                .onItem().ifNull().failWith(new BaseException(Response.Status.NOT_FOUND));
    }

    public Uni<Publisher> updateOne(String id, UpdatePublisher updatePublisher) {
        return validator.entityValidation(updatePublisher)
                .flatMap(validatedUpdatePublisher -> findOneById(id)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong publisher id")))
                .map(mapToPublisherFromUpdatePublisher(updatePublisher))
                .flatMap(publisher -> transactionManager.startTransaction()
                        .appendOperation(objects -> publisherRepository.updateOne(objects.getItem1(), publisher))
                        .appendOperation(objects -> bookService.updatePublisherReference(objects.getItem1(), id, updatePublisher.getName()))
                        .commit().map(updateResult -> publisher));
    }



    public Uni<BulkWriteResult> updateNumberOfBooks(ClientSession clientSession, String idForIncrement, String idForDecrement) {
        return publisherRepository.updateNumberOfBooks(clientSession, idForIncrement, idForDecrement);
    }

    public Uni<Publisher> incrementNumberOfBooksPublished(ClientSession clientSession, String id) {
        return publisherRepository.incrementNumberOfBooksPublished(clientSession, id);
    }

    public Uni<Publisher> decrementNumberOfBooksPublished(ClientSession clientSession, String id) {
        return publisherRepository.decrementNumberOfBooksPublished(clientSession, id);
    }

    public Uni<CustomResponse> deleteOne(String id) {
        return findOneById(id)
                .onFailure().transform(throwable -> new BadRequestException("Failure"))
                .flatMap(publisher -> bookService.findBookIdsByPublisherId(id)
                        .flatMap(checkIfPublisherIsInAnyBook()))
                .flatMap(bookIds -> publisherRepository.deleteOne(id)
                        .map(deleteResult -> new CustomResponse(Response.Status.OK, "Success")));
    }

    //region VALIDATORS
    public Function<List<String>, Uni<? extends List<String>>> checkIfPublisherIsInAnyBook() {
        return bookIds -> {
            if (bookIds.size() > 0) {
                return Uni.createFrom().failure(new BadRequestException("There is at least one book with the given publisher, so you can not delete the publisher"));
            } else {
                return Uni.createFrom().item(bookIds);
            }
        };
    }

    public Function<User, Uni<? extends User>> checkIfUserIsInternal() {
        return user -> {
            if (user.getRole().equals(Role.ADMIN)) {
                return Uni.createFrom().item(user);
            } else {
                return Uni.createFrom().failure(new BadRequestException("The user that creates the publisher should be internal user"));
            }
        };
    }

    public Function<Publisher, Publisher> mapToPublisherFromUpdatePublisher(UpdatePublisher updatePublisher) {
        return publisher -> {
            publisher.setName(updatePublisher.getName());
            return publisher;
        };
    }

    //endregion
}
