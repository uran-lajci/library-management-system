package org.kodelabs.borrow;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.kodelabs.book.BookService;
import org.kodelabs.book.models.BookInfo;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.mongoDb.transactions.TransactionManager;
import org.kodelabs.request.books.RequestService;
import org.kodelabs.users.Role;
import org.kodelabs.users.UpdateUser;
import org.kodelabs.users.UserService;
import org.kodelabs.users.models.UserInfo;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

import static java.time.LocalDate.now;

@ApplicationScoped
public class BorrowService {

    @Inject
    TransactionManager transactionManager;

    @Inject
    Validator validator;

    @Inject
    RequestService requestService;

    @Inject
    UserService userService;

    @Inject
    BookService bookService;

    @Inject
    BorrowRepository borrowRepository;

    public static Borrow mapToBorrow(CreateBorrow createBorrow, BookInfo bookInfo, UserInfo userInfo) {
        return new Borrow(bookInfo, userInfo, createBorrow.getStartDate(), createBorrow.getEndDate(), State.BORROWED);
    }

    public Uni<Borrow> addOne(CreateBorrow createBorrow) {
        return validator.entityValidation(createBorrow)
                .flatMap(checkIfStartDateIsBeforeEndDate())
                .flatMap(checkIfStartDateIsCurrentDate())
                .flatMap(borrow -> userService.findOneByIdAndRole(createBorrow.getInternalUserId(), Role.ADMIN)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id for Internal User")))
                .flatMap(user -> userService.findOneByIdAndRole(createBorrow.getExternalUserId(), Role.USER)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id for External User")))
                .map(user -> userService.mapToUserInfo.apply(user))
                .flatMap(userInfo -> bookService.findOneById(createBorrow.getBookId())
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id for book"))
                        .map(book -> bookService.mapToBookInfo.apply(book))
                        .map(bookInfo -> mapToBorrow(createBorrow, bookInfo, userInfo)))
                .flatMap(checkIfBookIsAvailableToBeBorrowedInTheGivenTime())
                .flatMap(borrow -> transactionManager.startTransaction()
                        .appendOperation(objects -> borrowRepository.addOne(objects.getItem1().unWrap(), borrow))
                        .appendOperation(objects -> bookService.decrementNumberOfBooksAvailable(
                                objects.getItem1().unWrap(), createBorrow.getBookId()).map(book -> objects.getItem2()))
                        .commit());
    }

    public Uni<IdsOfBorrowedBooksOfUser> getIdsOfBooksOfUser(String userId, State state) {
        if (state.equals(State.BORROWED)) {
            return borrowRepository.getIdsOfBorrowedBooksOfUser(userId, State.BORROWED)
                    .onItem().ifNull().failWith(new BaseException(Response.Status.NOT_FOUND, "There are no borrowed books"));
        } else if (state.equals(State.RETURNED)) {
            return borrowRepository.getIdsOfBorrowedBooksOfUser(userId, State.RETURNED)
                    .onItem().ifNull().failWith(new BaseException(Response.Status.NOT_FOUND, "There are no returned books"));
        } else {
            return Uni.createFrom().failure(new BadRequestException("State should be only BORROWED or RETURNED"));
        }
    }

    public Uni<Borrow> findIfBookHasBeenReturnedFromUser(String bookId, String userId) {
        return borrowRepository.findBorrowFromBookAndUser(bookId, userId)
                .flatMap(borrow -> {
                    if (borrow.getState().equals(State.RETURNED)) {
                        return Uni.createFrom().item(borrow);
                    } else {
                        return Uni.createFrom().failure(new BaseException(Response.Status.NOT_FOUND, "The book has not been returned"));
                    }
                });
    }

    public Uni<List<BookInfoAndCount>> getTheMostReadBooksInTheLastMonth() {
        return borrowRepository.getTheMostReadBooksInTheLastMonth();
    }

    public Uni<Borrow> updateStateToReturned(String userId, String adminId, String id) {
        return userService.findOneByIdAndRole(adminId, Role.ADMIN)
                .onFailure().transform(throwable -> new BadRequestException("Wrong id for Internal User"))
                .flatMap(user -> userService.findOneByIdAndRole(userId, Role.USER)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong id for External User")))
                .flatMap(user -> borrowRepository.findBorrowedBookFromUser(id, userId)
                        .onItem().ifNull().failWith(new BadRequestException("There are not borrowed books from this user")))
                .flatMap(checkIfEndDateIsCurrentDate())
                .flatMap(borrow -> transactionManager.startTransaction()
                                .appendOperation(objects -> borrowRepository.updateState(objects.getItem1().unWrap(), id, State.RETURNED))
                                .appendOperation(objects -> bookService.incrementNumberOfBooksAvailable(
                                        objects.getItem1().unWrap(), objects.getItem2().getBook().getId()).map(book -> objects.getItem2()))
                                .commit());
    }

    public Uni<UpdateResult> updateUserReference(ClientSession clientSession, String userId, UpdateUser updateUser) {
        return borrowRepository.updateUserReference(clientSession, userId, updateUser);
    }

    public Function<CreateBorrow, Uni<? extends CreateBorrow>> checkIfStartDateIsBeforeEndDate() {
        return createBorrow -> {
            if (createBorrow.getStartDate().isAfter(createBorrow.getEndDate())) {
                return Uni.createFrom().failure(new BadRequestException("Start date should be before end date."));
            } else {
                return Uni.createFrom().item(createBorrow);
            }
        };
    }

    public Function<CreateBorrow, Uni<? extends CreateBorrow>> checkIfStartDateIsCurrentDate() {
        return createBorrow -> {
            if (createBorrow.getStartDate().equals(now())) {
                return Uni.createFrom().item(createBorrow);
            } else {
                return Uni.createFrom().failure(new BadRequestException("You should come to borrow the book in " + createBorrow.getStartDate()));
            }
        };
    }

    public Function<Borrow, Uni<? extends Borrow>> checkIfEndDateIsCurrentDate() {
        return borrow -> {
            if (borrow.getEndDate().equals(now())) {
                return Uni.createFrom().item(borrow);
            } else {
                return Uni.createFrom().failure(new BadRequestException("You should come to return the book in " + borrow.getEndDate()));
            }
        };
    }

    public Function<Borrow, Uni<? extends Borrow>> checkIfBookIsAvailableToBeBorrowedInTheGivenTime() {
        return borrow -> requestService.checkIfUserHasReservedBook(borrow)    // reserved means that user requested book and his request got approved
                .flatMap(request -> {
                    if (request == null) {
                        return requestService.checkBookAvailabilityForBorrow(borrow).map(checkedRequest -> borrow);
                    } else {
                        return Uni.createFrom().item(borrow);
                    }
                });
    }

}
