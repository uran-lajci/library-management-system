package org.kodelabs.providers.interceptors.responses;

import org.kodelabs.entities.exceptions.BaseException;

import java.util.List;

/** Created by Gentrit Gojani on 8/2/18. */
public class ErrorResponse {
  public Error error;

  public ErrorResponse(BaseException baseException) {
    this.error = new Error(baseException);
  }

  public static class Error {
    public int code;
    public String message;
    public List<BaseException.Field> fields;

    public Error(BaseException baseException) {
      this.code = baseException.code;
      this.message = baseException.getMessage();
      this.fields = baseException.fields;
    }
  }
}
