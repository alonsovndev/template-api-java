package com.alonsovndev.template.features.health.presentation;

import com.alonsovndev.template.features.health.application.CheckReadiness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

  private final CheckReadiness checkReadiness;

  public HealthController(CheckReadiness checkReadiness) {
    this.checkReadiness = checkReadiness;
  }

  /** Liveness: no dependency checks, so a database outage never recycles the process. */
  @GetMapping
  public HealthResponse liveness() {
    return new HealthResponse("ok");
  }

  @GetMapping("/ready")
  public ResponseEntity<ReadinessResponse> readiness() {
    if (checkReadiness.execute().ready()) {
      return ResponseEntity.ok(new ReadinessResponse("ready"));
    }
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
        .body(new ReadinessResponse("not_ready"));
  }
}
