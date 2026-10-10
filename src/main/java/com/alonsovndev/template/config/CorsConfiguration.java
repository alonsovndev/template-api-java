package com.alonsovndev.template.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** CORS is disabled unless explicit origins are configured; wildcards are rejected at binding. */
@Configuration(proxyBeanMethods = false)
public class CorsConfiguration implements WebMvcConfigurer {

  private final AppProperties properties;

  public CorsConfiguration(AppProperties properties) {
    this.properties = properties;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    String[] origins = properties.cors().allowedOrigins().toArray(String[]::new);
    if (origins.length == 0) {
      return;
    }
    registry
        .addMapping("/**")
        .allowedOrigins(origins)
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
        .allowedHeaders("Authorization", "Content-Type")
        .allowCredentials(false);
  }
}
