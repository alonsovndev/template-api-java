package com.alonsovndev.template.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.alonsovndev.template.features.health.infrastructure.PostgresReadinessProbe;
import com.alonsovndev.template.support.PostgresTestDatabase;
import com.alonsovndev.template.support.items.ItemRepositoryContract;
import com.alonsovndev.template.support.items.domain.ItemRepository;
import com.alonsovndev.template.support.items.infrastructure.JdbcItemRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;

@Tag("postgres")
class PostgresRepositoryTest extends ItemRepositoryContract {

  private static PostgresTestDatabase database;

  @BeforeAll
  static void migrate() {
    database = PostgresTestDatabase.open();
    database.migrate(
        PostgresTestDatabase.PRODUCTION_LOCATION, PostgresTestDatabase.FIXTURE_LOCATION);
  }

  @AfterAll
  static void dispose() {
    database.close();
  }

  @Override
  protected ItemRepository repository() {
    return new JdbcItemRepository(JdbcClient.create(database.dataSource()));
  }

  @Test
  void readinessProbeSeesTheDatabase() {
    assertThat(new PostgresReadinessProbe(database.dataSource()).isReady()).isTrue();
  }

  @Test
  void databaseRejectsBlankNamesIndependentlyOfTheDomain() {
    JdbcClient jdbc = JdbcClient.create(database.dataSource());

    org.assertj.core.api.Assertions.assertThatThrownBy(
            () ->
                jdbc.sql("INSERT INTO items (identifier, name) VALUES (gen_random_uuid(), '  ')")
                    .update())
        .hasMessageContaining("ck_items_name_not_blank");
  }
}
