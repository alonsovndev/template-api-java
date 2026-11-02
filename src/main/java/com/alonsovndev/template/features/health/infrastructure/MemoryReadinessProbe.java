package com.alonsovndev.template.features.health.infrastructure;

import com.alonsovndev.template.features.health.application.ReadinessProbe;

/** Memory mode has no external dependency, so it is ready once the application is running. */
public class MemoryReadinessProbe implements ReadinessProbe {

  @Override
  public boolean isReady() {
    return true;
  }
}
