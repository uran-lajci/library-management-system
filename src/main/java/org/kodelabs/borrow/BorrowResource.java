package org.kodelabs.borrow;

import io.smallrye.mutiny.Uni;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/borrows")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BorrowResource {

    @Inject
    BorrowService borrowService;

    @POST
    public Uni<Borrow> add(CreateBorrow createBorrow) {
        return borrowService.addOne(createBorrow);
    }

    @PUT
    @Path("/{id}")
    public Uni<Borrow> updateState(@QueryParam("userId") String userId, @QueryParam("adminId") String adminId, @PathParam("id") String id) {
        return borrowService.updateStateToReturned(userId, adminId, id);
    }

}
