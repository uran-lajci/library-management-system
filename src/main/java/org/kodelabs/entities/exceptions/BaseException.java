package org.kodelabs.entities.exceptions;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.kodelabs.utils.CommonUtils.notBlank;

public class BaseException extends CompletionException {
  public int code;
  public String message;

  public List<Field> fields;

  public String methodName;

  public BaseException() {}

  public BaseException(Response.Status status) {
    super(status.getReasonPhrase());
    this.code = status.getStatusCode();
  }

  public BaseException(Response.StatusType statusInfo) {
    super(statusInfo.getReasonPhrase());
    this.code = statusInfo.getStatusCode();
  }

  public BaseException(Response.StatusType statusInfo, String message) {
    super(statusInfo.getReasonPhrase());
    this.code = statusInfo.getStatusCode();
    this.message = message;
  }

  public BaseException(int code) {
    super("Not found exception!");
    this.code = code;
    this.message = "Not found exception!";
  }

  public BaseException(int code, String message) {
    super(message);
    this.code = code;
    this.message = message;
  }

  public BaseException(int code, String message, List<Field> fields) {
    super(message);
    this.code = code;
    this.message = message;
    this.fields = fields;
  }

  public BaseException(Throwable throwable) {
    super(throwable);
    this.message = throwable.getMessage();
  }

  public BaseException(String methodName, Throwable throwable) {
    super(throwable);
    this.methodName = methodName;
  }

  public BaseException(String methodName, int code, String message) {
    super(message);
    this.methodName = methodName;
    this.code = code;
    this.message = message;
  }

  public BaseException withMessage(String message) {
    this.message = message;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BaseException that = (BaseException) o;
    return code == that.code;
  }

  public String toStringValue() {
    return String.format(
        "error_type: BaseException, methodName: %s, message: %s",
        notBlank(methodName) ? methodName : "Unknown", message);
  }

  public static class Field {
    public String name;
    public String error;

    public Field(String name, String error) {
      this.name = name;
      this.error = error;
    }
  }
}
