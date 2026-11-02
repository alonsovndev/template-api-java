package com.alonsovndev.template.config;

/** Startup configuration error. Messages name fields only and never include values. */
public class ConfigurationException extends RuntimeException {

  public ConfigurationException(String message) {
    super(message);
  }
}
