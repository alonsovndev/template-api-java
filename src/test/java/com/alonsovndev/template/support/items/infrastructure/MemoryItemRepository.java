package com.alonsovndev.template.support.items.infrastructure;

import com.alonsovndev.template.support.items.domain.DuplicateItemException;
import com.alonsovndev.template.support.items.domain.Item;
import com.alonsovndev.template.support.items.domain.ItemRepository;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MemoryItemRepository implements ItemRepository {

  private final ConcurrentMap<UUID, Item> items = new ConcurrentHashMap<>();

  @Override
  public void add(Item item) {
    if (items.putIfAbsent(item.identifier(), item) != null) {
      throw new DuplicateItemException();
    }
  }

  @Override
  public Optional<Item> get(UUID identifier) {
    return Optional.ofNullable(items.get(identifier));
  }
}
