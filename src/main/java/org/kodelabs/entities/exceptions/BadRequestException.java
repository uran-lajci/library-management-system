package org.kodelabs.entities.exceptions;

import java.util.List;

public class BadRequestException extends BaseException {
  public BadRequestException(String message) {
    super(400, message);
  }

  public BadRequestException(Throwable throwable) {
    super(400, throwable.getMessage());
  }

  public BadRequestException(Integer errorCode, String message) {
    super(errorCode, message);
  }

  public BadRequestException(Integer errorCode, String message, List<Field> fields) {
    super(errorCode, message, fields);
  }
}
