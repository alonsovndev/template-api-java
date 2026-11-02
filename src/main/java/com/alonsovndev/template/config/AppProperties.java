package com.alonsovndev.template.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

/** Immutable startup settings bound from the active environment profile. */
@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
    @NotNull @Pattern(regexp = "^(local|dev|test|container|prod)$") String environment,
    @NotBlank String name,
    @NotBlank String version,
    @NotNull @Valid Persistence persistence,
    @NotNull @Valid @DefaultValue Cors cors) {

  private static final String ORIGIN_PATTERN =
      "^https?://([A-Za-z0-9.-]+|\\[[0-9A-Fa-f:]+\\])(:\\d{1,5})?$";

  public enum Driver {
    MEMORY,
    POSTGRESQL
  }

  public enum SslMode {
    DISABLE,
    REQUIRE
  }

  /** NULL targets transaction-mode poolers (Supabase, PgBouncer) and Lambda. */
  public enum PoolMode {
    QUEUE,
    NULL
  }

  public record Persistence(
      @NotNull Driver driver,
      @NotNull @Valid Postgres postgres,
      @NotEmpty List<String> flywayLocations) {}

  public record Postgres(
      String host,
      @Min(1) @Max(65535) int port,
      String username,
      String password,
      String database,
      @NotNull SslMode sslMode,
      @NotNull PoolMode poolMode,
      String credentialsJson) {

    @Override
    public String toString() {
      return "Postgres[host=" + host + ", port=" + port + ", database=" + database + "]";
    }
  }

  public record Cors(List<@Pattern(regexp = ORIGIN_PATTERN) String> allowedOrigins) {

    public Cors {
      allowedOrigins = allowedOrigins == null ? List.of() : List.copyOf(allowedOrigins);
    }
  }
}
