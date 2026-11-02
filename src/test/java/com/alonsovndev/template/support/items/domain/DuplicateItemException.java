package com.alonsovndev.template.support.items.domain;

public class DuplicateItemException extends RuntimeException {

  public DuplicateItemException(Throwable cause) {
    super("Item already exists", cause);
  }

  public DuplicateItemException() {
    super("Item already exists");
  }
}
