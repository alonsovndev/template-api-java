package com.alonsovndev.template.features.health.application;

public class CheckReadiness {

  private final ReadinessProbe probe;

  public CheckReadiness(ReadinessProbe probe) {
    this.probe = probe;
  }

  public ReadinessResult execute() {
    return new ReadinessResult(probe.isReady());
  }
}
