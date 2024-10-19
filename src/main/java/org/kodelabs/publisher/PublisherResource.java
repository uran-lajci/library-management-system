package org.kodelabs.publisher;

import io.smallrye.mutiny.Uni;
import org.kodelabs.response.CustomResponse;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/publishers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class PublisherResource {

    @Inject
    PublisherService publisherService;

    @POST
    public Uni<Publisher> add(CreatePublisher createPublisher, @QueryParam("userId") String userId) {
        return publisherService.addOne(createPublisher, userId);
    }

    @GET
    @Path("/{id}")
    public Uni<Publisher> getOneById(@PathParam("id") String id) {
        return publisherService.findOneById(id);
    }

    @PUT
    @Path("/{id}")
    public Uni<Publisher> update(@PathParam("id") String id, UpdatePublisher updatePublisher) {
        return publisherService.updateOne(id, updatePublisher);
    }

    @DELETE
    @Path("/{id}")
    public Uni<CustomResponse> delete(@PathParam("id") String id) {
        return publisherService.deleteOne(id);
    }

}
