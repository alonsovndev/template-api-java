package com.alonsovndev.template.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** The application must start and stay alive while PostgreSQL is unreachable. */
@SpringBootTest(
    properties = {
      "app.persistence.driver=postgresql",
      "app.persistence.postgres.host=127.0.0.1",
      "app.persistence.postgres.port=1",
      "app.persistence.postgres.username=app",
      "app.persistence.postgres.password=secret",
      "app.persistence.postgres.database=app"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostgresOutageTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void livenessStaysHealthyWhileReadinessFails() throws Exception {
    mockMvc.perform(get("/health")).andExpect(status().isOk());
    mockMvc.perform(get("/health/ready")).andExpect(status().isServiceUnavailable());
  }
}
