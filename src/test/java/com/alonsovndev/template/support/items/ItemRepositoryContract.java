package com.alonsovndev.template.support.items;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alonsovndev.template.support.items.application.CreateItem;
import com.alonsovndev.template.support.items.application.CreateItemCommand;
import com.alonsovndev.template.support.items.application.GetItem;
import com.alonsovndev.template.support.items.application.ItemResult;
import com.alonsovndev.template.support.items.domain.DuplicateItemException;
import com.alonsovndev.template.support.items.domain.Item;
import com.alonsovndev.template.support.items.domain.ItemRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Every {@link ItemRepository} adapter must pass the same behaviour. */
public abstract class ItemRepositoryContract {

  protected abstract ItemRepository repository();

  @Test
  void roundTripsAnItem() {
    UUID identifier = UUID.randomUUID();
    repository().add(new Item(identifier, "Widget"));

    assertThat(repository().get(identifier)).contains(new Item(identifier, "Widget"));
  }

  @Test
  void returnsEmptyForUnknownItem() {
    assertThat(repository().get(UUID.randomUUID())).isEmpty();
  }

  @Test
  void rejectsDuplicateIdentifier() {
    UUID identifier = UUID.randomUUID();
    repository().add(new Item(identifier, "Widget"));

    assertThatThrownBy(() -> repository().add(new Item(identifier, "Other")))
        .isInstanceOf(DuplicateItemException.class);
  }

  @Test
  void useCasesCreateAndFetchThroughThePort() {
    UUID identifier = UUID.randomUUID();
    ItemResult created =
        new CreateItem(repository()).execute(new CreateItemCommand(identifier, "Gadget"));

    assertThat(created).isEqualTo(new ItemResult(identifier, "Gadget"));
    assertThat(new GetItem(repository()).execute(identifier)).contains(created);
    assertThat(new GetItem(repository()).execute(UUID.randomUUID())).isEmpty();
  }
}
