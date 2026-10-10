package com.alonsovndev.template.features.health.application;

/** Port: reports whether the backing infrastructure can serve requests. Must never throw. */
public interface ReadinessProbe {

  boolean isReady();
}
