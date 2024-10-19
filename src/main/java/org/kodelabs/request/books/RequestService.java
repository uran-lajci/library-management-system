package org.kodelabs.request.books;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.kodelabs.book.Book;
import org.kodelabs.book.BookService;
import org.kodelabs.book.models.BookInfo;
import org.kodelabs.borrow.Borrow;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.request.books.models.IdsOfRequestedBooksFromUser;
import org.kodelabs.request.books.models.StatusModel;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.users.Role;
import org.kodelabs.users.UpdateUser;
import org.kodelabs.users.User;
import org.kodelabs.users.UserService;
import org.kodelabs.users.models.UserInfo;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.function.Function;

@ApplicationScoped
public class RequestService {

    @Inject
    UserService userService;

    @Inject
    BookService bookService;

    @Inject
    Validator validator;

    @Inject
    RequestRepository requestRepository;

    //region MAPPERS

    public static Request mapToRequest(CreateRequest createRequest, BookInfo bookInfo, UserInfo userInfo) {
        return new Request(createRequest.getStartDate(), createRequest.getEndDate(), Status.PENDING, bookInfo, userInfo);
    }
    //endregion

    //region ADDERS

    public Uni<Request> addOne(CreateRequest createRequest) {
        return validator.entityValidation(createRequest)
                .flatMap(checkIfStartDateIsBeforeEndDate())
                .flatMap(validatedCreateRequest -> userService.findOneById(validatedCreateRequest.getUserId())
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id user")))
                .flatMap(validateCountingRequestsOfUserAndUserRole())
                .map(user -> userService.mapToUserInfo.apply(user))
                .flatMap(userInfo -> bookService.findOneById(createRequest.getBookId())
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id for book"))
                        .map(book -> bookService.mapToBookInfo.apply(book))
                        .map(bookInfo -> mapToRequest(createRequest, bookInfo, userInfo)))
                .flatMap(request -> checkBookAvailabilityForReservation(createRequest)
                        .map(requestFromCollection -> request))
                .flatMap(request -> requestRepository.addOne(request));
    }
    //endregion

    //region GETTERS

    public Uni<PageModel<Request>> getList(RequestQueryParameters requestQueryParameters, PaginationQuery paginationQuery) {
        return requestRepository.getList(requestQueryParameters, paginationQuery);
    }

    public Uni<IdsOfRequestedBooksFromUser> getIdsOfBooksOfUser(String userId) {
        return requestRepository.getIdsOfBooksOfUser(userId)
                .onItem().ifNull().failWith(() -> new BaseException(Response.Status.NOT_FOUND, "There is no book of user with id " + userId));
    }

    public Uni<Request> findOneById(String id) {
        return requestRepository.findOneById(id)
                .onItem().ifNull().failWith(() -> new BaseException(Response.Status.NOT_FOUND, "There is no user with id " + id));
    }

    //endregion

    //region UPDATES

    public Uni<CustomResponse> updateOne(String requestId, String userId, StatusModel statusModel) {
        return validator.entityValidation(statusModel)
                .flatMap(validatedStatusModel -> requestRepository.findOneById(requestId)
                        .onItem().ifNull().failWith(() -> new BadRequestException("Wrong request id")))
                .flatMap(request -> bookService.findOneById(request.getBook().getId())
                        .onFailure().transform(throwable -> new BadRequestException("Wrong requestId for book"))
                        .flatMap(book -> userService.findOneById(userId)
                                .onFailure().transform(throwable -> new BadRequestException("Wrong userId for user"))
                                .flatMap(user -> updateStatus(book, user, statusModel, request)))
                );
    }

    public Uni<CustomResponse> updateStatus(Book book, User user, StatusModel statusModel, Request request) {
        if (user.getRole().equals(Role.ADMIN) && statusModel.getStatus().equals(Status.APPROVED) && !request.getStatus().toString().equals(Status.APPROVED.name())
                && book.getNumberOfCopies() > 0) {
            request.setStatus(Status.APPROVED);
        } else if (user.getRole().equals(Role.ADMIN) && statusModel.getStatus().equals(Status.REJECTED) && !request.getStatus().toString().equals(Status.REJECTED.name())) {
            request.setStatus(Status.REJECTED);
        } else if (user.getRole().equals(Role.USER) && statusModel.getStatus().equals(Status.CANCELED) && !request.getStatus().equals(Status.CANCELED)) {
            request.setStatus(Status.CANCELED);
        } else {
            return Uni.createFrom().failure(new BadRequestException("Failure"));
        }
        return requestRepository.updateStatus(request).map(updatedRequest -> new CustomResponse(Response.Status.OK, "Success"));
    }

    public Uni<UpdateResult> updateUserReference(ClientSession clientSession, String userId, UpdateUser updateUser) {
        return requestRepository.updateUserReference(clientSession, userId, updateUser);
    }
    //endregion

    //region DELETES

    public Uni<Request> deleteOne(ClientSession clientSession, String bookId) {
        return requestRepository.deleteOne(clientSession, bookId);
    }

    //endregion

    //region VALIDATES
    public Uni<Request> checkBookAvailabilityForReservation(CreateRequest createRequest) {
        return bookService.findOneById(createRequest.getBookId())
                .flatMap(book ->
                        requestRepository.getRequestsOfBookInStateApprovedInTheSpecifiedTime(createRequest.getBookId(), createRequest.getStartDate(), createRequest.getEndDate())
                                .flatMap(requests -> {
                                    if (book.getNumberOfCopies() > requests.size()) {
                                        return requestRepository.checkBookAvailabilityInTimeline(
                                                createRequest.getBookId(), createRequest.getStartDate(), createRequest.getEndDate());
                                    } else {
                                        return Uni.createFrom().failure(new BadRequestException("There are no books in stock in the specified time"));
                                    }
                                }));
    }

    public Uni<Request> checkBookAvailabilityForBorrow(Borrow borrow) {
        return bookService.findOneById(borrow.getBook().getId())
                .flatMap(book ->
                        requestRepository.getRequestsOfBookInStateApprovedInTheSpecifiedTime(borrow.getBook().getId(), borrow.getStartDate(), borrow.getEndDate())
                                .flatMap(requestsApproved -> {
                                    if (book.getNumberOfBooksAvailable() > requestsApproved.size()) {
                                        return requestRepository.checkBookAvailabilityInTimeline(borrow.getBook().getId(), borrow.getStartDate(), borrow.getEndDate());
                                    } else {
                                        return Uni.createFrom().failure(new BadRequestException("There are no books in available in the specified time"));
                                    }
                                }));
    }

    public Uni<Request> checkIfUserHasReservedBook(Borrow borrow) {
        return requestRepository.checkIfUserHasReservedBook(borrow);
    }

    public Function<CreateRequest, Uni<? extends CreateRequest>> checkIfStartDateIsBeforeEndDate() {
        return createRequest -> {
            if (createRequest.getStartDate().isAfter(createRequest.getEndDate())) {
                return Uni.createFrom().failure(new BadRequestException("Start date should be before end date."));
            } else {
                return Uni.createFrom().item(createRequest);
            }
        };
    }

    public Function<User, Uni<? extends User>> validateCountingRequestsOfUserAndUserRole() {
        return user -> {
            if (user.getRole().equals(Role.USER)) {
                return requestRepository.countPendingRequestsOfUser(user._id)
                        .flatMap(requestsPending -> {
                            if (requestsPending >= 3) {
                                return Uni.createFrom().failure(new BadRequestException("You can't request more then 3 books without response"));
                            } else {
                                return Uni.createFrom().item(requestsPending);
                            }
                        }).map(requests -> user);
            } else {
                return Uni.createFrom().failure(new BadRequestException("User should be external"));
            }
        };
    }

    //endregion

}
