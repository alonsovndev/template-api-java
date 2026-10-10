package com.alonsovndev.template.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.alonsovndev.template.config.AppProperties;
import com.alonsovndev.template.config.AppProperties.Driver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

class AppPropertiesBindingTest {

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties(AppProperties.class)
  static class PropertiesConfiguration {}

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withUserConfiguration(PropertiesConfiguration.class)
          .withPropertyValues(
              "app.environment=local",
              "app.name=template",
              "app.version=1",
              "app.persistence.driver=memory",
              "app.persistence.flyway-locations=classpath:db/migration",
              "app.persistence.postgres.host=localhost",
              "app.persistence.postgres.port=5432",
              "app.persistence.postgres.password=s3cret",
              "app.persistence.postgres.ssl-mode=disable",
              "app.persistence.postgres.pool-mode=queue");

  @Test
  void bindsValidSettingsAndDefaultsCorsToDisabled() {
    runner.run(
        context -> {
          AppProperties properties = context.getBean(AppProperties.class);
          assertThat(properties.persistence().driver()).isEqualTo(Driver.MEMORY);
          assertThat(properties.cors().allowedOrigins()).isEmpty();
        });
  }

  @Test
  void bindsExplicitOrigins() {
    runner
        .withPropertyValues(
            "app.cors.allowed-origins=https://a.example.com, http://localhost:5173, http://[::1]:3000")
        .run(
            context ->
                assertThat(context.getBean(AppProperties.class).cors().allowedOrigins())
                    .containsExactly(
                        "https://a.example.com", "http://localhost:5173", "http://[::1]:3000"));
  }

  @Test
  void rejectsWildcardOrigins() {
    for (String origin : new String[] {"*", "https://*.example.com", "http://*"}) {
      runner
          .withPropertyValues("app.cors.allowed-origins=" + origin)
          .run(context -> assertThat(context).as(origin).hasFailed());
    }
  }

  @Test
  void rejectsOriginsWithPathsOrMissingScheme() {
    for (String origin :
        new String[] {"https://a.example.com/", "a.example.com", "https://a.example.com/path"}) {
      runner
          .withPropertyValues("app.cors.allowed-origins=" + origin)
          .run(context -> assertThat(context).as(origin).hasFailed());
    }
  }

  @Test
  void rejectsUnknownOrDifferentlyCasedEnvironment() {
    for (String environment : new String[] {"staging", "PROD", "Prod"}) {
      runner
          .withPropertyValues("app.environment=" + environment)
          .run(context -> assertThat(context).as(environment).hasFailed());
    }
  }

  @Test
  void failureDoesNotLeakSecrets() {
    runner
        .withPropertyValues("app.cors.allowed-origins=*")
        .run(
            context -> {
              Throwable failure = context.getStartupFailure();
              for (Throwable cause = failure; cause != null; cause = cause.getCause()) {
                assertThat(String.valueOf(cause.getMessage())).doesNotContain("s3cret");
              }
            });
  }

  @Test
  void rejectsOutOfRangePort() {
    runner
        .withPropertyValues("app.persistence.postgres.port=70000")
        .run(context -> assertThat(context).hasFailed());
  }
}
