package com.alonsovndev.template.support.items.application;

import com.alonsovndev.template.support.items.domain.Item;
import com.alonsovndev.template.support.items.domain.ItemRepository;

public class CreateItem {

  private final ItemRepository repository;

  public CreateItem(ItemRepository repository) {
    this.repository = repository;
  }

  public ItemResult execute(CreateItemCommand command) {
    Item item = new Item(command.identifier(), command.name());
    repository.add(item);
    return new ItemResult(item.identifier(), item.name());
  }
}
