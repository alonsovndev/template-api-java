package com.alonsovndev.template.shared.infrastructure;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;

/** Applies SQL migrations. Run as an explicit step, never during normal application startup. */
public class FlywayMigrator {

  private final DataSource dataSource;
  private final String[] locations;

  public FlywayMigrator(DataSource dataSource, String... locations) {
    this.dataSource = dataSource;
    this.locations = locations;
  }

  public int migrate() {
    return Flyway.configure()
        .dataSource(dataSource)
        .locations(locations)
        .load()
        .migrate()
        .migrationsExecuted;
  }
}
