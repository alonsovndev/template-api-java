package com.alonsovndev.template.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.alonsovndev.template.features.health.application.ReadinessProbe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthEndpointTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReadinessProbe readinessProbe;

  @Test
  void livenessReportsOk() throws Exception {
    mockMvc
        .perform(get("/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ok"));
  }

  @Test
  void readinessReportsReady() throws Exception {
    org.mockito.Mockito.when(readinessProbe.isReady()).thenReturn(true);

    mockMvc
        .perform(get("/health/ready"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ready"));
  }

  @Test
  void readinessReports503WhileLivenessStaysHealthy() throws Exception {
    org.mockito.Mockito.when(readinessProbe.isReady()).thenReturn(false);

    mockMvc
        .perform(get("/health/ready"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.status").value("not_ready"));
    mockMvc.perform(get("/health")).andExpect(status().isOk());
  }

  @Test
  void unknownRoutesReturnProblemDetails() throws Exception {
    mockMvc
        .perform(get("/does-not-exist"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(404));
  }
}
