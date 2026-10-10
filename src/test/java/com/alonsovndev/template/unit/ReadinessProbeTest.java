package com.alonsovndev.template.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alonsovndev.template.features.health.infrastructure.MemoryReadinessProbe;
import com.alonsovndev.template.features.health.infrastructure.PostgresReadinessProbe;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class ReadinessProbeTest {

  @Test
  void memoryProbeIsAlwaysReady() {
    assertThat(new MemoryReadinessProbe().isReady()).isTrue();
  }

  @Test
  void postgresProbeIsReadyWhenQuerySucceeds() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    Connection connection = mock(Connection.class);
    Statement statement = mock(Statement.class);
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.createStatement()).thenReturn(statement);

    assertThat(new PostgresReadinessProbe(dataSource).isReady()).isTrue();
    verify(statement).setQueryTimeout(2);
    verify(statement).execute("SELECT 1");
  }

  @Test
  void postgresProbeIsNotReadyWhenConnectionFails() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    when(dataSource.getConnection()).thenThrow(new SQLException("down"));

    assertThat(new PostgresReadinessProbe(dataSource).isReady()).isFalse();
  }

  @Test
  void postgresProbeIsNotReadyWhenQueryFails() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    Connection connection = mock(Connection.class);
    Statement statement = mock(Statement.class);
    when(dataSource.getConnection()).thenReturn(connection);
    when(connection.createStatement()).thenReturn(statement);
    when(statement.execute(anyString())).thenThrow(new SQLException("timeout"));

    assertThat(new PostgresReadinessProbe(dataSource).isReady()).isFalse();
  }

  @Test
  void postgresProbeIsNotReadyOnUnexpectedRuntimeFailure() throws SQLException {
    DataSource dataSource = mock(DataSource.class);
    when(dataSource.getConnection()).thenThrow(new IllegalStateException("pool closed"));

    assertThat(new PostgresReadinessProbe(dataSource).isReady()).isFalse();
  }
}
