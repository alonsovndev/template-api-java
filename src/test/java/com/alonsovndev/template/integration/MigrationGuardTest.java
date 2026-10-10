package com.alonsovndev.template.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.alonsovndev.template.TemplateApplication;
import com.alonsovndev.template.config.ConfigurationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

class MigrationGuardTest {

  @Test
  void migrateStepFailsInsteadOfSilentlySucceedingInMemoryMode() {
    SpringApplicationBuilder builder =
        new SpringApplicationBuilder(TemplateApplication.class).profiles("test");

    Throwable failure =
        catchThrowable(
            () ->
                builder
                    .run(
                        "--app.migrate=true",
                        "--app.persistence.driver=memory",
                        "--spring.main.web-application-type=none")
                    .close());

    assertThat(failure).isNotNull();
    Throwable root = failure;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    assertThat(root)
        .isInstanceOf(ConfigurationException.class)
        .hasMessage("Invalid configuration fields: app.persistence.driver");
  }

  private static Throwable catchThrowable(Runnable action) {
    try {
      action.run();
      return null;
    } catch (RuntimeException exception) {
      return exception;
    }
  }
}
