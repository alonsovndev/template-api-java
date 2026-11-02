package com.alonsovndev.template.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

class DocsExposureTest {

  private static final String MEMORY = "app.persistence.driver=memory";

  abstract static class DocsContract {
    @Autowired MockMvc mockMvc;

    void assertApiDocs(boolean exposed) throws Exception {
      mockMvc
          .perform(get("/v3/api-docs"))
          .andExpect(exposed ? status().isOk() : status().isNotFound());
    }
  }

  @Nested
  @SpringBootTest(properties = MEMORY)
  @AutoConfigureMockMvc
  @ActiveProfiles("local")
  class Local extends DocsContract {
    @Test
    void exposesDocs() throws Exception {
      assertApiDocs(true);
    }
  }

  @Nested
  @SpringBootTest(properties = MEMORY)
  @AutoConfigureMockMvc
  @ActiveProfiles("container")
  class Container extends DocsContract {
    @Test
    void exposesDocs() throws Exception {
      assertApiDocs(true);
    }
  }

  @Nested
  @SpringBootTest(properties = MEMORY)
  @AutoConfigureMockMvc
  @ActiveProfiles("dev")
  class Dev extends DocsContract {
    @Test
    void hidesDocs() throws Exception {
      assertApiDocs(false);
    }
  }

  @Nested
  @SpringBootTest(properties = {MEMORY, "app.persistence.postgres.host=localhost"})
  @AutoConfigureMockMvc
  @ActiveProfiles("prod")
  class Prod extends DocsContract {
    @Test
    void hidesDocs() throws Exception {
      assertApiDocs(false);
    }
  }
}
