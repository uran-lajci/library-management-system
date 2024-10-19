package org.kodelabs.entities.exceptions;


import static org.kodelabs.utils.CommonUtils.notBlank;

public class ForbiddenException extends BaseException {
  public ForbiddenException(String methodName, String message) {
    super(methodName, 400, message);
  }

  @Override
  public String toStringValue() {
    return String.format(
        "error_type: AuthenticationException, methodName: %s, message: %s",
        notBlank(methodName) ? methodName : "Unknown", message);
  }
}
