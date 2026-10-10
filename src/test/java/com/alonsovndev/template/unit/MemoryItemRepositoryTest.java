package com.alonsovndev.template.unit;

import com.alonsovndev.template.support.items.ItemRepositoryContract;
import com.alonsovndev.template.support.items.domain.ItemRepository;
import com.alonsovndev.template.support.items.infrastructure.MemoryItemRepository;

class MemoryItemRepositoryTest extends ItemRepositoryContract {

  private final ItemRepository repository = new MemoryItemRepository();

  @Override
  protected ItemRepository repository() {
    return repository;
  }
}
