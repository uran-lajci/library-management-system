package org.kodelabs.users;

import io.smallrye.mutiny.Uni;
import org.kodelabs.borrow.BorrowService;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.mongoDb.transactions.TransactionManager;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.request.books.RequestService;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.review.ReviewService;
import org.kodelabs.users.models.UserInfo;
import org.kodelabs.users.models.UsersByRole;
import org.kodelabs.validation.ValidationMethods;
import org.kodelabs.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.function.Function;

@ApplicationScoped
public class UserService {
    //region MAPPERS

    public Function<CreateUser, User> mapToUser = createUser ->
            new User(createUser.getFirstName(), createUser.getLastName(), createUser.getUsername(),
                    createUser.getPassword(), createUser.getRole(), createUser.getPhoneNumber(), createUser.getAddress());

    public Function<User, UserInfo> mapToUserInfo = user ->
            new UserInfo(user._id, user.getFirstName(), user.getLastName());
    @Inject
    TransactionManager transactionManager;
    //endregion
    @Inject
    ValidationMethods validationMethods;
    @Inject
    Validator validator;

    @Inject
    UserRepository userRepository;
    @Inject
    ReviewService reviewService;
    @Inject
    BorrowService borrowService;
    @Inject
    RequestService requestService;

    public Function<User, User> mapToUserFromUpdateUser(UpdateUser updateUser) {
        return user -> {
            user.setFirstName(updateUser.getFirstName());
            user.setLastName(updateUser.getLastName());
            user.setUsername(updateUser.getUsername());
            user.setPassword(updateUser.getPassword());
            user.setPhoneNumber(updateUser.getPhoneNumber());
            user.setAddress(updateUser.getAddress());
            return user;
        };
    }

    //region ADDERS

    public Uni<User> addOne(CreateUser createUser) {
        return validator.entityValidation(createUser)
                .map(validatedCreateUser -> mapToUser.apply(validatedCreateUser))
                .flatMap(user -> userRepository.addOne(user));
    }
    //endregion

    //region GETTERS

    public Uni<User> findOneById(String id) {
        return userRepository.findOneById(id)
                .onItem().ifNull().failWith(() -> new BaseException(Response.Status.NOT_FOUND, "There is no user with id " + id));
    }

    public Uni<User> findOneByIdAndRole(String id, Role role) {
        return userRepository.findOneById(id)
                .flatMap(user -> {
                    if (user.getRole().equals(role)) {
                        return Uni.createFrom().item(user);
                    } else {
                        return Uni.createFrom().failure(new BaseException(Response.Status.NOT_FOUND, "There is no user with id " + id + " and role " + role));
                    }
                });
    }

    public Uni<PageModel<User>> getList(UserQueryParameters userQueryParameters, PaginationQuery paginationQuery) {
        return userRepository.getList(userQueryParameters, paginationQuery);
    }

    public Uni<List<UsersByRole>> getUsersGroupedByRole() {
        return userRepository.getUsersGroupedByRole();
    }
    //endregion

    //region UPDATES

    public Uni<User> update(String id, UpdateUser updateUser) {
        return validator.entityValidation(updateUser)
                .flatMap(validatedUpdateUser -> findOneById(id)
                        .onFailure().transform(throwable -> new BadRequestException("Wrong user id")))
                .map(mapToUserFromUpdateUser(updateUser))
                .flatMap(user -> userRepository.updateOne(id, user))
                .flatMap(updatedUser -> transactionManager.startTransaction()
                        .appendOperation(objects -> reviewService.updateUserReference(objects.getItem1().unWrap(), id, updateUser))
                        .appendOperation(objects -> requestService.updateUserReference(objects.getItem1().unWrap(), id, updateUser))
                        .appendOperation(objects -> borrowService.updateUserReference(objects.getItem1().unWrap(), id, updateUser))
                        .commit().map(updateResult -> updatedUser));
    }

    //region DELETES

    public Uni<CustomResponse> deleteOne(String id) {
        return userRepository.deleteOne(id)
                .flatMap(validationMethods.checkForNull());
    }
    //endregion

}
