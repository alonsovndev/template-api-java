package com.alonsovndev.template.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.alonsovndev.template.config.AppProperties.PoolMode;
import com.alonsovndev.template.config.AppProperties.Postgres;
import com.alonsovndev.template.config.AppProperties.SslMode;
import com.alonsovndev.template.config.ConfigurationException;
import com.alonsovndev.template.shared.infrastructure.PostgresConnection;
import com.alonsovndev.template.shared.infrastructure.PostgresDataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

class PostgresConnectionTest {

  private static Postgres settings(
      String username,
      String password,
      SslMode sslMode,
      PoolMode poolMode,
      String credentialsJson) {
    return new Postgres(
        "db.example.com", 5432, username, password, "app", sslMode, poolMode, credentialsJson);
  }

  @Test
  void buildsPlainUrlForQueueMode() {
    PostgresConnection connection =
        PostgresConnection.from(settings("app", "secret", SslMode.DISABLE, PoolMode.QUEUE, ""));

    assertThat(connection.jdbcUrl())
        .isEqualTo("jdbc:postgresql://db.example.com:5432/app?sslmode=disable");
    assertThat(connection.username()).isEqualTo("app");
    assertThat(connection.password()).isEqualTo("secret");
  }

  @Test
  void requiresSslAndDisablesServerSidePreparedStatementsForPoolers() {
    PostgresConnection connection =
        PostgresConnection.from(settings("app", "secret", SslMode.REQUIRE, PoolMode.NULL, null));

    assertThat(connection.jdbcUrl()).endsWith("?sslmode=require&prepareThreshold=0");
  }

  @Test
  void credentialsJsonOverridesUsernameAndPassword() {
    PostgresConnection connection =
        PostgresConnection.from(
            settings(
                "ignored",
                "ignored",
                SslMode.DISABLE,
                PoolMode.QUEUE,
                "{\"username\":\"rds_user\",\"password\":\"rds_pass\"}"));

    assertThat(connection.username()).isEqualTo("rds_user");
    assertThat(connection.password()).isEqualTo("rds_pass");
  }

  @Test
  void reportsMissingFieldsWithoutValues() {
    assertThatThrownBy(
            () -> PostgresConnection.from(settings("", "", SslMode.DISABLE, PoolMode.QUEUE, "")))
        .isInstanceOf(ConfigurationException.class)
        .hasMessage(
            "Invalid configuration fields: app.persistence.postgres.username,"
                + " app.persistence.postgres.password");
  }

  @Test
  void rejectsHostWithUrlSyntax() {
    Postgres hostile =
        new Postgres(
            "db/evil?x=1", 5432, "app", "secret", "app", SslMode.DISABLE, PoolMode.QUEUE, "");

    assertThatThrownBy(() -> PostgresConnection.from(hostile))
        .hasMessage("Invalid configuration fields: app.persistence.postgres.host");
  }

  @Test
  void malformedCredentialsJsonNeverEchoesContent() {
    assertThatThrownBy(
            () ->
                PostgresConnection.from(
                    settings(
                        "app", "secret", SslMode.DISABLE, PoolMode.QUEUE, "{not-json hunter2")))
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Invalid configuration fields: app.persistence.postgres.credentials-json")
        .message()
        .doesNotContain("hunter2");
  }

  @Test
  void credentialsJsonMissingKeysIsReportedAsMissingFields() {
    assertThatThrownBy(
            () ->
                PostgresConnection.from(
                    settings("app", "secret", SslMode.DISABLE, PoolMode.QUEUE, "{}")))
        .hasMessageContaining("username")
        .hasMessageContaining("password");
  }

  @Test
  void toStringHidesCredentials() {
    PostgresConnection connection =
        PostgresConnection.from(settings("app", "secret", SslMode.DISABLE, PoolMode.QUEUE, ""));

    assertThat(connection.toString()).doesNotContain("secret");
    assertThat(settings("app", "secret", SslMode.DISABLE, PoolMode.QUEUE, "").toString())
        .doesNotContain("secret");
  }

  @Test
  void dataSourceIsLazyAndLimitedToOneConnectionBehindPoolers() {
    PostgresConnection connection =
        PostgresConnection.from(
            new Postgres(
                "127.0.0.1", 1, "app", "secret", "app", SslMode.DISABLE, PoolMode.NULL, ""));

    try (HikariDataSource dataSource = PostgresDataSourceFactory.create(connection)) {
      assertThat(dataSource.getMaximumPoolSize()).isEqualTo(1);
      assertThat(dataSource.getMinimumIdle()).isZero();
    }
  }
}
