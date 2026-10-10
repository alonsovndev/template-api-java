package com.alonsovndev.template.support.items.domain;

import java.util.Optional;
import java.util.UUID;

/** Port. Implementations raise {@link DuplicateItemException} when the identifier exists. */
public interface ItemRepository {

  void add(Item item);

  Optional<Item> get(UUID identifier);
}
