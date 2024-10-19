package org.kodelabs.entities.exceptions;

public class NotFoundException extends BaseException {
  public NotFoundException() {
    super(404);
  }
}
