package org.kodelabs.users;

import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import org.kodelabs.mongoDb.MongoDb;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.users.models.UsersByRole;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@ApplicationScoped
public class UserRepository {

    @Inject
    MongoDb mongoDb;

    public ReactiveMongoCollection<User> getCollection() {
        return mongoDb.getCollection("users", User.class);
    }

    public Uni<User> addOne(User user) {
        user.generateId();
        user.createdAt = new Date();
        return getCollection().insertOne(user).map(insertOneResult -> user);
    }

    public Uni<PageModel<User>> getList(UserQueryParameters userQueryParameters, PaginationQuery paginationQuery) {
        return PageModel.mapToPageModel(getCollection(), userQueryParameters.toBson(), paginationQuery);
    }

    public Uni<List<User>> getList() {
        return getCollection().find().collect().asList();
    }

    public Uni<User> findOneById(String id) {
        return getCollection().find(eq(User.FIELD_ID, id)).collect().first();
    }

    public Uni<List<UsersByRole>> getUsersGroupedByRole() {
        return getCollection().aggregate(
                List.of(new Document("$group",
                        new Document(User.FIELD_ID, "$role")
                                .append(User.FIELD_ROLE,
                                        new Document("$first", "$role"))
                                .append("user",
                                        new Document("$push",
                                                new Document(User.FIELD_ID, "$_id")
                                                        .append(User.FIELD_FIRST_NAME, "$firstName")
                                                        .append(User.FIELD_LAST_NAME, "$lastName"))))),
                UsersByRole.class).collect().asList();
    }

    public Uni<User> updateOne(String id, User user) {
        return getCollection().replaceOne(eq(User.FIELD_ID, id), user).map(updateResult -> user);
    }

    public Uni<User> deleteOne(String id) {
        return getCollection().findOneAndDelete(eq(User.FIELD_ID, id));
    }
}
