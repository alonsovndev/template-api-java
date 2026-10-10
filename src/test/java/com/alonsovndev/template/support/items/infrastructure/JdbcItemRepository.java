package com.alonsovndev.template.support.items.infrastructure;

import com.alonsovndev.template.support.items.domain.DuplicateItemException;
import com.alonsovndev.template.support.items.domain.Item;
import com.alonsovndev.template.support.items.domain.ItemRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;

/** Explicit row-to-entity mapping keeps the domain {@code Item} free of persistence concerns. */
public class JdbcItemRepository implements ItemRepository {

  private final JdbcClient jdbc;

  public JdbcItemRepository(JdbcClient jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public void add(Item item) {
    try {
      jdbc.sql("INSERT INTO items (identifier, name) VALUES (:identifier, :name)")
          .param("identifier", item.identifier())
          .param("name", item.name())
          .update();
    } catch (DuplicateKeyException exception) {
      throw new DuplicateItemException(exception);
    }
  }

  @Override
  public Optional<Item> get(UUID identifier) {
    return jdbc.sql("SELECT identifier, name FROM items WHERE identifier = :identifier")
        .param("identifier", identifier)
        .query(
            (rows, rowNumber) ->
                new Item(rows.getObject("identifier", UUID.class), rows.getString("name")))
        .optional();
  }
}
