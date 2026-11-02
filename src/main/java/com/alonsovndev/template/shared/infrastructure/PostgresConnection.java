package com.alonsovndev.template.shared.infrastructure;

import com.alonsovndev.template.config.AppProperties.PoolMode;
import com.alonsovndev.template.config.AppProperties.Postgres;
import com.alonsovndev.template.config.AppProperties.SslMode;
import com.alonsovndev.template.config.ConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import tools.jackson.databind.json.JsonMapper;

/** Validated, secret-safe PostgreSQL connection settings resolved from {@link Postgres}. */
public record PostgresConnection(
    String jdbcUrl, String username, String password, PoolMode poolMode) {

  private static final Pattern IDENTIFIER = Pattern.compile("^[A-Za-z0-9_.-]+$");
  private static final JsonMapper JSON = JsonMapper.builder().build();

  /**
   * Credentials from {@code credentialsJson} ({@code {"username","password"}}, the RDS secret
   * format) override username and password.
   */
  public static PostgresConnection from(Postgres settings) {
    String username = settings.username();
    String password = settings.password();
    if (hasText(settings.credentialsJson())) {
      Map<?, ?> credentials = parseCredentials(settings.credentialsJson());
      username = asText(credentials.get("username"));
      password = asText(credentials.get("password"));
    }

    List<String> invalid = new ArrayList<>();
    check(invalid, "app.persistence.postgres.host", settings.host());
    check(invalid, "app.persistence.postgres.database", settings.database());
    if (!hasText(username)) {
      invalid.add("app.persistence.postgres.username");
    }
    if (!hasText(password)) {
      invalid.add("app.persistence.postgres.password");
    }
    if (!invalid.isEmpty()) {
      throw new ConfigurationException(
          "Invalid configuration fields: " + String.join(", ", invalid));
    }
    return new PostgresConnection(jdbcUrl(settings), username, password, settings.poolMode());
  }

  private static String jdbcUrl(Postgres settings) {
    StringBuilder url =
        new StringBuilder("jdbc:postgresql://")
            .append(settings.host())
            .append(':')
            .append(settings.port())
            .append('/')
            .append(settings.database())
            .append("?sslmode=")
            .append(settings.sslMode() == SslMode.REQUIRE ? "require" : "disable");
    if (settings.poolMode() == PoolMode.NULL) {
      url.append("&prepareThreshold=0");
    }
    return url.toString();
  }

  private static Map<?, ?> parseCredentials(String json) {
    try {
      return JSON.readValue(json, Map.class);
    } catch (RuntimeException exception) {
      throw new ConfigurationException(
          "Invalid configuration fields: app.persistence.postgres.credentials-json");
    }
  }

  private static void check(List<String> invalid, String field, String value) {
    if (!hasText(value) || !IDENTIFIER.matcher(value).matches()) {
      invalid.add(field);
    }
  }

  private static boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private static String asText(Object value) {
    return value instanceof String text ? text : null;
  }

  @Override
  public String toString() {
    return "PostgresConnection[jdbcUrl=" + jdbcUrl + "]";
  }
}
