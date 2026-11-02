package com.alonsovndev.template.unit;

import static org.assertj.core.api.Assertions.assertThat;

import com.alonsovndev.template.features.health.application.CheckReadiness;
import org.junit.jupiter.api.Test;

class CheckReadinessTest {

  @Test
  void reportsReadyWhenProbeIsReady() {
    assertThat(new CheckReadiness(() -> true).execute().ready()).isTrue();
  }

  @Test
  void reportsNotReadyWhenProbeIsNotReady() {
    assertThat(new CheckReadiness(() -> false).execute().ready()).isFalse();
  }
}
