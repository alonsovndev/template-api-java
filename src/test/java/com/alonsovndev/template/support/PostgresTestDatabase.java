package com.alonsovndev.template.support;

import com.alonsovndev.template.config.AppProperties.PoolMode;
import com.alonsovndev.template.config.AppProperties.Postgres;
import com.alonsovndev.template.config.AppProperties.SslMode;
import com.alonsovndev.template.shared.infrastructure.FlywayMigrator;
import com.alonsovndev.template.shared.infrastructure.PostgresConnection;
import com.alonsovndev.template.shared.infrastructure.PostgresDataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Disposable PostgreSQL started by scripts/test-postgres.sh. It refuses to touch any database that
 * does not carry the throwaway test identity, because it drops the public schema.
 */
public final class PostgresTestDatabase implements AutoCloseable {

  static final String PROJECT_PREFIX = "template-api-test-";
  static final String USERNAME = "template_test";
  static final String DATABASE = "template_api_test";
  static final String HOST = "127.0.0.1";

  public static final String PRODUCTION_LOCATION = "classpath:db/migration";
  public static final String FIXTURE_LOCATION = "classpath:db/fixture";

  private final HikariDataSource dataSource;

  private PostgresTestDatabase(HikariDataSource dataSource) {
    this.dataSource = dataSource;
  }

  public static PostgresTestDatabase open() {
    PostgresTestDatabase database =
        new PostgresTestDatabase(
            PostgresDataSourceFactory.create(PostgresConnection.from(settings(PoolMode.QUEUE))));
    database.reset();
    return database;
  }

  /** Command-line arguments that point a full application start at the disposable database. */
  public static String[] applicationArguments(PoolMode poolMode, String... extra) {
    Postgres settings = settings(poolMode);
    java.util.List<String> arguments =
        new java.util.ArrayList<>(
            java.util.List.of(
                "--app.persistence.driver=postgresql",
                "--app.persistence.postgres.host=" + settings.host(),
                "--app.persistence.postgres.port=" + settings.port(),
                "--app.persistence.postgres.username=" + settings.username(),
                "--app.persistence.postgres.password=" + settings.password(),
                "--app.persistence.postgres.database=" + settings.database(),
                "--app.persistence.postgres.pool-mode=" + poolMode.name().toLowerCase(),
                "--app.persistence.flyway-locations="
                    + PRODUCTION_LOCATION
                    + ","
                    + FIXTURE_LOCATION));
    arguments.addAll(java.util.List.of(extra));
    return arguments.toArray(String[]::new);
  }

  private static Postgres settings(PoolMode poolMode) {
    String project = requireVariable("TEMPLATE_TEST_PROJECT");
    if (!project.startsWith(PROJECT_PREFIX)) {
      throw new IllegalStateException("Refusing to run: not a disposable test project");
    }
    return new Postgres(
        HOST,
        Integer.parseInt(requireVariable("TEMPLATE_TEST_POSTGRES_PORT")),
        USERNAME,
        requireVariable("TEMPLATE_TEST_PASSWORD"),
        DATABASE,
        SslMode.DISABLE,
        poolMode,
        "");
  }

  public HikariDataSource dataSource() {
    return dataSource;
  }

  public void reset() {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      statement.execute("DROP SCHEMA public CASCADE");
      statement.execute("CREATE SCHEMA public");
    } catch (SQLException exception) {
      throw new IllegalStateException("Could not reset the disposable database", exception);
    }
  }

  public void migrate(String... locations) {
    new FlywayMigrator(dataSource, locations).migrate();
  }

  @Override
  public void close() {
    if (!dataSource.isClosed()) {
      reset();
      dataSource.close();
    }
  }

  private static String requireVariable(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Missing " + name + "; run scripts/test-postgres.sh");
    }
    return value;
  }
}
