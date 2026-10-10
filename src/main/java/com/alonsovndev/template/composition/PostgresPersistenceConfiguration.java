package com.alonsovndev.template.composition;

import com.alonsovndev.template.config.AppProperties;
import com.alonsovndev.template.features.health.application.ReadinessProbe;
import com.alonsovndev.template.features.health.infrastructure.PostgresReadinessProbe;
import com.alonsovndev.template.shared.infrastructure.PostgresConnection;
import com.alonsovndev.template.shared.infrastructure.PostgresDataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires PostgreSQL adapters. Active only when {@code app.persistence.driver=postgresql}. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.persistence.driver", havingValue = "postgresql")
public class PostgresPersistenceConfiguration {

  @Bean(destroyMethod = "close")
  HikariDataSource dataSource(AppProperties properties) {
    return PostgresDataSourceFactory.create(
        PostgresConnection.from(properties.persistence().postgres()));
  }

  @Bean
  ReadinessProbe postgresReadinessProbe(DataSource dataSource) {
    return new PostgresReadinessProbe(dataSource);
  }
}
