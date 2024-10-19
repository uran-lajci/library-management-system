package org.kodelabs.users;

import io.smallrye.mutiny.Uni;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.response.CustomResponse;
import org.kodelabs.users.models.UsersByRole;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @POST
    public Uni<User> add(CreateUser createUser) {
        return userService.addOne(createUser);
    }

    @GET
    public Uni<PageModel<User>> getList(@BeanParam UserQueryParameters userQueryParameters, @BeanParam PaginationQuery paginationQuery) {
        return userService.getList(userQueryParameters, paginationQuery);
    }

    @GET
    @Path("/group-by-role")
    public Uni<List<UsersByRole>> getUsersGroupedByRole() {
        return userService.getUsersGroupedByRole();
    }

    @PUT
    @Path("/{id}")
    public Uni<User> update(@PathParam("id") String id, UpdateUser updateUser) {
        return userService.update(id, updateUser);
    }

    @DELETE
    @Path("/{id}")
    public Uni<CustomResponse> delete(@PathParam("id") String id) {
        return userService.deleteOne(id);
    }


}
