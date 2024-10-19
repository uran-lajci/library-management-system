package org.kodelabs.request.books;

import io.smallrye.mutiny.Uni;
import org.kodelabs.pagination.PageModel;
import org.kodelabs.pagination.PaginationQuery;
import org.kodelabs.request.books.models.StatusModel;
import org.kodelabs.response.CustomResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/requests")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class RequestResource {

    @Inject
    RequestService requestService;

    @POST
    public Uni<Request> add(CreateRequest request) {
        return requestService.addOne(request);
    }

    @GET
    public Uni<PageModel<Request>> getList(@BeanParam RequestQueryParameters requestQueryParameters, @BeanParam PaginationQuery paginationQuery) {
        return requestService.getList(requestQueryParameters, paginationQuery);
    }

    @PUT
    @Path("/{id}/status")
    public Uni<CustomResponse> updateStatus(@PathParam("id") String id, @QueryParam("userId") String userId, StatusModel status) {
        return requestService.updateOne(id, userId, status);
    }

}
