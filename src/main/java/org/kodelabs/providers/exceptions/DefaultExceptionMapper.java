package org.kodelabs.providers.exceptions;

import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.reactivex.exceptions.CompositeException;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.kodelabs.entities.exceptions.BadRequestException;
import org.kodelabs.entities.exceptions.BaseException;
import org.kodelabs.exceptions.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.kodelabs.utils.CommonUtils.notBlank;

/**
 * Created by Gentrit Gojani on 8/2/18.
 */
public class DefaultExceptionMapper {
    private static final Logger logs = LoggerFactory.getLogger(DefaultExceptionMapper.class);

    @ServerExceptionMapper
    public Uni<Response> toResponse(Throwable exception) {
        return Uni.createFrom()
                .emitter(
                        uniEmitter -> {
                            BaseException baseException;

                            // everything should return _BaseException OR a subclass of _BaseException
                            if (exception instanceof BaseException) {
                                baseException = (BaseException) exception;
                            } else if (exception instanceof MongoWriteException) {
                                if (((MongoWriteException) exception).getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                                    baseException = new BaseException(400, formatMongodbDuplicateErrorMessage(exception));
                                } else {
                                    baseException = new BaseException(400, exception.getMessage());
                                }

                            } else if (exception instanceof CompositeException) {
                                CompositeException compositeException = (CompositeException) exception;

                                // get the last exception for user
                                Throwable throwable =
                                        compositeException.getExceptions().get(compositeException.size() - 1);
                                if (throwable instanceof BaseException) {
                                    baseException = (BaseException) throwable;
                                } else {
                                    baseException = new BadRequestException(throwable.getMessage());
                                }

                                // print all exceptions
                                compositeException
                                        .getExceptions()
                                        .forEach(throwable1 -> logs.error(exception.getMessage(), exception));
                            } else if (exception instanceof StatusRuntimeException) {
                                StatusRuntimeException statusRuntimeException = (StatusRuntimeException) exception;
                                Status status = statusRuntimeException.getStatus();
                                Response.Status statusCode =
                                        ExceptionHandler.grpcToHttpStatusCode(status.getCode());
                                baseException =
                                        new BaseException(
                                                statusCode.getStatusCode(),
                                                notBlank(status.getDescription())
                                                        ? status.getDescription()
                                                        : statusRuntimeException.getMessage());
                            } else { // when something unexpected happens
                                baseException =
                                        new BaseException(
                                                Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                                exception.getMessage());

                                if (!ignore(exception)) {
                                    if (printTrance(exception)) {
                                        logs.error(
                                                notBlank(exception.getMessage()) ? exception.getMessage() : "Exception: ",
                                                exception);
                                    } else {
                                        logMessage(exception);
                                    }
                                }
                            }


                            uniEmitter.complete(
                                    Response.status(baseException.code)
                                            .entity(baseException)
                                            .type(MediaType.APPLICATION_JSON)
                                            .build());
                        });
    }

    private void logMessage(Throwable exception) {
        if (exception.getMessage() != null) {
            logs.error(exception.getMessage());
        } else {
            exception.printStackTrace();
        }
    }

    // exceptions that we don't need to have traces
    private boolean printTrance(Throwable exception) {
        return !(exception instanceof javax.ws.rs.NotAllowedException
                || exception instanceof javax.ws.rs.NotFoundException);
    }

    private boolean ignore(Throwable exception) {
        return exception instanceof java.io.IOException
                && exception.getMessage() != null
                && exception
                .getMessage()
                .equals("java.io.IOException: io.vertx.core.VertxException: Connection was closed");
    }

    private String formatMongodbDuplicateErrorMessage(Throwable exception) {
        int startIndexForCollectionName = ((MongoWriteException) exception).getError().getMessage().indexOf("collection: ");
        int endIndexForCollectionName = ((MongoWriteException) exception).getError().getMessage().indexOf(" index");
        String collectionName = ((MongoWriteException) exception).getError().getMessage().substring(
                startIndexForCollectionName, endIndexForCollectionName);

        int startIndex = ((MongoWriteException) exception).getError().getMessage().indexOf("index:");
        int endIndex = ((MongoWriteException) exception).getError().getMessage().indexOf("dup key:");
        String key = ((MongoWriteException) exception).getError().getMessage().substring(startIndex, endIndex);

        return "Duplicate key error on " + key + "on " + collectionName;
    }
}