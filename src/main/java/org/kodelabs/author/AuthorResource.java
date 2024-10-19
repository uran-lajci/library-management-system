package org.kodelabs.author;

import io.smallrye.mutiny.Uni;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.response.CustomResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/authors")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @Inject
    AuthorService authorService;

    @POST
    public Uni<Author> add(CreateAuthor createAuthor) {
        return authorService.add(createAuthor);
    }

    @GET
    public Uni<PageModel<Author>> getList(@BeanParam AuthorQueryParameters authorQueryParameters, @BeanParam PaginationQuery paginationQuery) {
        return authorService.getList(authorQueryParameters, paginationQuery);
    }

    @GET
    @Path("/{id}")
    public Uni<Author> getById(@PathParam("id") String id) {
        return authorService.findOneById(id);
    }

    @PUT
    @Path("/{id}")
    public Uni<Author> update(@PathParam("id") String id, AuthorUpdate authorUpdate) {
        return authorService.updateOne(id, authorUpdate);
    }

    @DELETE
    @Path("/{id}")
    public Uni<CustomResponse> delete(@PathParam("id") String id) {
        return authorService.delete(id);
    }
}
