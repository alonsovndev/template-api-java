package com.alonsovndev.template.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.alonsovndev.template.TemplateApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;

class PostgresMisconfigurationTest {

  @Test
  void failsFastWithFieldNamesOnlyWhenCredentialsAreMissing() {
    SpringApplicationBuilder builder =
        new SpringApplicationBuilder(TemplateApplication.class)
            .web(WebApplicationType.NONE)
            .profiles("test");
    String[] arguments = {
      "--app.persistence.driver=postgresql",
      "--app.persistence.postgres.database=app",
      "--app.persistence.postgres.password="
    };

    Throwable failure = null;
    try {
      builder.run(arguments).close();
    } catch (RuntimeException exception) {
      failure = exception;
    }

    assertThat(failure).isNotNull();
    Throwable root = failure;
    while (root.getCause() != null) {
      root = root.getCause();
    }
    assertThat(root.getMessage())
        .isEqualTo(
            "Invalid configuration fields: app.persistence.postgres.username,"
                + " app.persistence.postgres.password");
  }
}
