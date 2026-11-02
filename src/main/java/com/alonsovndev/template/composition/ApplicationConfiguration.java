package com.alonsovndev.template.composition;

import com.alonsovndev.template.config.AppProperties;
import com.alonsovndev.template.features.health.application.CheckReadiness;
import com.alonsovndev.template.features.health.application.ReadinessProbe;
import com.alonsovndev.template.features.health.infrastructure.MemoryReadinessProbe;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root. Use cases and adapters are plain classes; this is the only place that decides
 * which adapter implements which port. Add new features here, one {@code @Bean} per use case.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AppProperties.class)
public class ApplicationConfiguration {

  @Bean
  @ConditionalOnProperty(name = "app.persistence.driver", havingValue = "memory")
  ReadinessProbe memoryReadinessProbe() {
    return new MemoryReadinessProbe();
  }

  @Bean
  CheckReadiness checkReadiness(ReadinessProbe readinessProbe) {
    return new CheckReadiness(readinessProbe);
  }
}
