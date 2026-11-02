package com.alonsovndev.template.composition;

import com.alonsovndev.template.config.AppProperties;
import com.alonsovndev.template.config.AppProperties.Driver;
import com.alonsovndev.template.config.ConfigurationException;
import com.alonsovndev.template.shared.infrastructure.FlywayMigrator;
import com.alonsovndev.template.shared.infrastructure.PostgresConnection;
import com.alonsovndev.template.shared.infrastructure.PostgresDataSourceFactory;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Explicit migration step: {@code java -jar app.jar --app.migrate=true
 * --spring.main.web-application-type=none}. Never active during a normal start. It opens its own
 * connections, so it needs a direct (not transaction-pooled) database connection.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "app.migrate", havingValue = "true")
public class MigrationConfiguration {

  @Bean
  ApplicationRunner migrationRunner(AppProperties properties) {
    if (properties.persistence().driver() != Driver.POSTGRESQL) {
      throw new ConfigurationException("Invalid configuration fields: app.persistence.driver");
    }
    PostgresConnection connection = PostgresConnection.from(properties.persistence().postgres());
    return arguments -> {
      try (HikariDataSource dataSource = PostgresDataSourceFactory.createForMigration(connection)) {
        new FlywayMigrator(
                dataSource, properties.persistence().flywayLocations().toArray(String[]::new))
            .migrate();
      }
    };
  }
}
