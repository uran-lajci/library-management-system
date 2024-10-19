package org.kodelabs.validation;

import io.smallrye.mutiny.Uni;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.response.CustomResponse;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import java.util.function.Function;
@Singleton
public class ValidationMethods {

    public <T> Function<T, Uni<? extends CustomResponse>> checkForNull() {
        return t -> {
            if(t == null) {
                return Uni.createFrom().failure(new BadRequestException("Failure"));
            }
            else {
                return Uni.createFrom().item(new CustomResponse(Response.Status.OK, "Success"));
            }
        };
    }
}
