package org.kodelabs.exceptions;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.kodelabs.entities.exceptions.BaseException;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static org.kodelabs.utils.CommonUtils.notBlank;
import static org.kodelabs.utils.CommonUtils.notNull;

public class ExceptionHandler {
  private static Map<Status.Code, Response.Status> statusesList =
      new HashMap<>() {
        {
          put(Status.Code.OK, Response.Status.OK);
          put(Status.Code.UNKNOWN, Response.Status.SERVICE_UNAVAILABLE);
          put(Status.Code.UNAVAILABLE, Response.Status.SERVICE_UNAVAILABLE);
          put(Status.Code.DEADLINE_EXCEEDED, Response.Status.INTERNAL_SERVER_ERROR);
          put(Status.Code.RESOURCE_EXHAUSTED, Response.Status.INTERNAL_SERVER_ERROR);
          put(Status.Code.INTERNAL, Response.Status.INTERNAL_SERVER_ERROR);
          put(Status.Code.ABORTED, Response.Status.INTERNAL_SERVER_ERROR);
          put(Status.Code.NOT_FOUND, Response.Status.NOT_FOUND);
          put(Status.Code.ALREADY_EXISTS, Response.Status.CONFLICT);
          put(Status.Code.PERMISSION_DENIED, Response.Status.FORBIDDEN);
          put(Status.Code.FAILED_PRECONDITION, Response.Status.PRECONDITION_FAILED);
          put(Status.Code.UNIMPLEMENTED, Response.Status.NOT_IMPLEMENTED);
          put(Status.Code.DATA_LOSS, Response.Status.REQUEST_TIMEOUT);
          put(Status.Code.UNAUTHENTICATED, Response.Status.FORBIDDEN);
          put(Status.Code.CANCELLED, Response.Status.BAD_REQUEST);
          put(Status.Code.INVALID_ARGUMENT, Response.Status.BAD_REQUEST);
          put(Status.Code.OUT_OF_RANGE, Response.Status.BAD_REQUEST);
        }
      };

  public static Response.Status grpcToHttpStatusCode(Status.Code grpcCode) {
    if (statusesList.containsKey(grpcCode)) {
      return statusesList.get(grpcCode);
    }
    return Response.Status.BAD_REQUEST;
  }

  public static Status.Code httpToGrpcStatusCode(Response.Status statusCode) {
    if (statusesList.containsValue(statusCode)) {
      return statusesList.entrySet().stream()
          .filter(codeStatusCodeEntry -> codeStatusCodeEntry.equals(statusCode))
          .map(Map.Entry::getKey)
          .findFirst()
          .get();
    }
    return Status.Code.INTERNAL;
  }

  public static BaseException handleException(Throwable throwable) {
    Status status = null;
    if (throwable instanceof StatusRuntimeException) {
      StatusRuntimeException exception = (StatusRuntimeException) throwable;
      status = exception.getStatus();
    }

    if (notNull(throwable.getCause()) && throwable.getCause() instanceof StatusRuntimeException) {
      StatusRuntimeException exception = (StatusRuntimeException) throwable.getCause();
      status = exception.getStatus();
    }
    if (notNull(status)) {
      return new BaseException(
          grpcToHttpStatusCode(status.getCode()).getStatusCode(),
          notBlank(status.getDescription()) ? status.getDescription() : throwable.getMessage());
    }
    return new BaseException(throwable);
  }

  public static Status exceptionToHttpMetadata(Throwable throwable) {
    Status status = null;
    if (throwable instanceof StatusRuntimeException) {
      StatusRuntimeException exception = (StatusRuntimeException) throwable;
      status = exception.getStatus();
    }

    if (notNull(throwable.getCause()) && throwable.getCause() instanceof StatusRuntimeException) {
      StatusRuntimeException exception = (StatusRuntimeException) throwable.getCause();
      status = exception.getStatus();
    }
    if (notNull(status)) {
      return Status.fromCode(status.getCode())
          .withCause(throwable)
          .withDescription(status.getDescription());
    }
    return Status.fromThrowable(throwable);
  }
}
