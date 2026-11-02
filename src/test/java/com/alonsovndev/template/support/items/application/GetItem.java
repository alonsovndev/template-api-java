package com.alonsovndev.template.support.items.application;

import com.alonsovndev.template.support.items.domain.ItemRepository;
import java.util.Optional;
import java.util.UUID;

public class GetItem {

  private final ItemRepository repository;

  public GetItem(ItemRepository repository) {
    this.repository = repository;
  }

  public Optional<ItemResult> execute(UUID identifier) {
    return repository.get(identifier).map(item -> new ItemResult(item.identifier(), item.name()));
  }
}
