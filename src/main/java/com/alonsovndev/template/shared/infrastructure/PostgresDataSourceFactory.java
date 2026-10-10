package com.alonsovndev.template.shared.infrastructure;

import com.alonsovndev.template.config.AppProperties.PoolMode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class PostgresDataSourceFactory {

  private static final long CONNECTION_TIMEOUT_MILLIS = 2_000;

  private PostgresDataSourceFactory() {}

  /** Lazy pool: the application starts while the database is down and readiness reports it. */
  public static HikariDataSource create(PostgresConnection connection) {
    HikariConfig config = baseConfig(connection);
    if (connection.poolMode() == PoolMode.NULL) {
      config.setMaximumPoolSize(1);
      config.setMinimumIdle(0);
    }
    return new HikariDataSource(config);
  }

  /** Flyway needs two connections (history table and migration), so pool mode is ignored. */
  public static HikariDataSource createForMigration(PostgresConnection connection) {
    HikariConfig config = baseConfig(connection);
    config.setMaximumPoolSize(2);
    config.setMinimumIdle(0);
    return new HikariDataSource(config);
  }

  private static HikariConfig baseConfig(PostgresConnection connection) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(connection.jdbcUrl());
    config.setUsername(connection.username());
    config.setPassword(connection.password());
    config.setConnectionTimeout(CONNECTION_TIMEOUT_MILLIS);
    config.setInitializationFailTimeout(-1);
    return config;
  }
}
