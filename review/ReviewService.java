package org.kodelabs.review;

import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.ClientSession;
import io.smallrye.mutiny.Uni;
import org.kodelabs.book.BookService;
import org.kodelabs.book.models.AverageRatingForBook;
import org.kodelabs.book.models.BookInfo;
import org.kodelabs.book.models.CommentsForBook;
import org.kodelabs.borrow.BorrowService;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.mongoDb.transactions.TransactionManager;
import org.kodelabs.users.Role;
import org.kodelabs.users.UpdateUser;
import org.kodelabs.users.User;
import org.kodelabs.users.UserService;
import org.kodelabs.users.models.UserInfo;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

@ApplicationScoped
public class ReviewService {

    @Inject
    TransactionManager transactionManager;

    @Inject
    BorrowService borrowService;

    @Inject
    UserService userService;

    @Inject
    Validator validator;

    @Inject
    BookService bookService;

    @Inject
    ReviewRepository reviewRepository;

    public static Review mapToReview(CreateReview createReview, BookInfo bookInfo, UserInfo userInfo) {
        return new Review(createReview.getAverageRating(), createReview.getComment(), bookInfo, userInfo);
    }

    public Uni<Review> add(String bookId, CreateReview createReview) {
        return validator.entityValidation(createReview)
                .flatMap(validatedCreateReview -> userService.findOneById(validatedCreateReview.getUserId())
                        .onFailure().transform(throwable -> new BadRequestException("Wrong user id")))
                .flatMap(checkIfUserIsExternal())
                .map(user -> userService.mapToUserInfo.apply(user))
                .flatMap(userInfo -> bookService.findOneById(bookId)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong book id"))
                        .flatMap(book -> borrowService.findIfBookHasBeenReturnedFromUser(bookId, createReview.getUserId())
                                .onFailure().transform(throwable -> new BadRequestException("The book has not been read"))
                                .map(borrow -> book))
                        .map(book -> bookService.mapToBookInfo.apply(book))
                        .map(bookInfo -> mapToReview(createReview, bookInfo, userInfo)))
                .flatMap(review -> transactionManager.startTransaction()
                        .appendOperation(objects -> reviewRepository.addOne(objects.getItem1().unWrap(), review))
                        .appendOperation(objects -> {
                            Uni<AverageRatingForBook> averageRating = getAverageRatingForBook(objects.getItem1().unWrap(), objects.getItem2().getBook().getId());
                            Uni<CommentsForBook> comments = getCommentsForBook(objects.getItem1().unWrap(), objects.getItem2().getBook().getId());

                            return averageRating.flatMap(averageRatingForBook ->
                                    comments.flatMap(commentsForBook -> bookService.updateAverageRatingAndComments(
                                            objects.getItem1().unWrap(), bookId, averageRatingForBook.getAverageRating(), commentsForBook.getComments())
                                    )).map(bookRating -> objects.getItem2());
                        }).commit()
                );
    }

    public Uni<AverageRatingForBook> getAverageRatingForBook(ClientSession clientSession, String bookId) {
        return reviewRepository.getAverageRatingForBook(clientSession, bookId);
    }

    public Uni<CommentsForBook> getCommentsForBook(ClientSession clientSession, String bookId) {
        return reviewRepository.getCommentsForBook(clientSession, bookId);
    }

    public Uni<UpdateResult> updateUserReference(ClientSession clientSession, String userId, UpdateUser updateUser) {
        return reviewRepository.updateUserReference(clientSession, userId, updateUser);
    }

    public Function<User, Uni<? extends User>> checkIfUserIsExternal() {
        return user -> {
            if (user.getRole().equals(Role.USER)) {
                return Uni.createFrom().item(user);
            } else {
                return Uni.createFrom().failure(new BadRequestException("The user should be external"));
            }
        };
    }


}
