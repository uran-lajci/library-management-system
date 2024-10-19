package org.kodelabs.entities.exceptions;

import static org.kodelabs.utils.CommonUtils.notBlank;

public class AuthenticationException extends BaseException {
  public AuthenticationException(String methodName) {
    super(methodName, 400, "Connector Down");
  }

  public AuthenticationException(String methodName, String message) {
    super(methodName, 400, message);
  }

  public AuthenticationException(String methodName, int errorCode, String message) {
    super(methodName, errorCode, message);
  }

  @Override
  public String toStringValue() {
    return String.format(
        "error_type: AuthenticationException, methodName: %s, message: %s",
        notBlank(methodName) ? methodName : "Unknown", message);
  }
}
