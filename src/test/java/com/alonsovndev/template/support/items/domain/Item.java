package com.alonsovndev.template.support.items.domain;

import java.util.Objects;
import java.util.UUID;

public record Item(UUID identifier, String name) {

  private static final int MAXIMUM_NAME_LENGTH = 100;

  public Item {
    Objects.requireNonNull(identifier, "identifier");
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Item name must not be blank");
    }
    if (name.length() > MAXIMUM_NAME_LENGTH) {
      throw new IllegalArgumentException("Item name must not exceed 100 characters");
    }
  }
}
