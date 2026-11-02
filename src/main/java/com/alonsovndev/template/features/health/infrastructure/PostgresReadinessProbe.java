package com.alonsovndev.template.features.health.infrastructure;

import com.alonsovndev.template.features.health.application.ReadinessProbe;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostgresReadinessProbe implements ReadinessProbe {

  static final int TIMEOUT_SECONDS = 2;
  private static final Logger LOGGER = LoggerFactory.getLogger(PostgresReadinessProbe.class);

  private final DataSource dataSource;

  public PostgresReadinessProbe(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public boolean isReady() {
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement()) {
      statement.setQueryTimeout(TIMEOUT_SECONDS);
      statement.execute("SELECT 1");
      return true;
    } catch (SQLException | RuntimeException exception) {
      LOGGER.warn("Readiness check failed: {}", exception.getClass().getSimpleName());
      return false;
    }
  }
}
