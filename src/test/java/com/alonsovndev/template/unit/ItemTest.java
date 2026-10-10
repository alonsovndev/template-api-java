package com.alonsovndev.template.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alonsovndev.template.support.items.domain.Item;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ItemTest {

  @Test
  void acceptsValidName() {
    assertThat(new Item(UUID.randomUUID(), "Widget").name()).isEqualTo("Widget");
  }

  @Test
  void rejectsBlankName() {
    assertThatThrownBy(() -> new Item(UUID.randomUUID(), "   "))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsNullName() {
    assertThatThrownBy(() -> new Item(UUID.randomUUID(), null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void rejectsOverlongName() {
    assertThatThrownBy(() -> new Item(UUID.randomUUID(), "x".repeat(101)))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void acceptsMaximumLengthName() {
    assertThat(new Item(UUID.randomUUID(), "x".repeat(100)).name()).hasSize(100);
  }
}
