package com.alonsovndev.template.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.alonsovndev.template.TemplateApplication;
import com.alonsovndev.template.config.AppProperties.PoolMode;
import com.alonsovndev.template.support.PostgresTestDatabase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.simple.JdbcClient;

@Tag("postgres")
class PostgresMigrationTest {

  private static PostgresTestDatabase database;

  @BeforeAll
  static void open() {
    database = PostgresTestDatabase.open();
  }

  @AfterAll
  static void dispose() {
    database.close();
  }

  @Test
  void productionMigrationsCreateNoBusinessTables() {
    database.reset();
    database.migrate(PostgresTestDatabase.PRODUCTION_LOCATION);

    JdbcClient jdbc = JdbcClient.create(database.dataSource());
    assertThat(
            jdbc.sql("SELECT count(*) FROM information_schema.tables WHERE table_name = 'items'")
                .query(Integer.class)
                .single())
        .isZero();
    assertThat(
            jdbc.sql("SELECT count(*) FROM flyway_schema_history WHERE success")
                .query(Integer.class)
                .single())
        .isEqualTo(1);
  }

  @Test
  void fixtureMigrationAddsTheExampleTableOnTop() {
    database.reset();
    database.migrate(
        PostgresTestDatabase.PRODUCTION_LOCATION, PostgresTestDatabase.FIXTURE_LOCATION);

    assertThat(
            JdbcClient.create(database.dataSource())
                .sql("SELECT count(*) FROM information_schema.tables WHERE table_name = 'items'")
                .query(Integer.class)
                .single())
        .isEqualTo(1);
  }

  @Test
  void migrationStepWorksBehindATransactionPooler() {
    database.reset();

    runApplication(PoolMode.NULL, "--app.migrate=true", "--spring.main.web-application-type=none");

    assertThat(tableCount("items")).isEqualTo(1);
  }

  @Test
  void normalStartDoesNotMigrate() {
    database.reset();

    runApplication(PoolMode.QUEUE, "--spring.main.web-application-type=none");

    assertThat(tableCount("flyway_schema_history")).isZero();
    assertThat(tableCount("items")).isZero();
  }

  private static void runApplication(PoolMode poolMode, String... extra) {
    new SpringApplicationBuilder(TemplateApplication.class)
        .profiles("test")
        .run(PostgresTestDatabase.applicationArguments(poolMode, extra))
        .close();
  }

  private static int tableCount(String table) {
    return JdbcClient.create(database.dataSource())
        .sql("SELECT count(*) FROM information_schema.tables WHERE table_name = :table")
        .param("table", table)
        .query(Integer.class)
        .single();
  }
}
