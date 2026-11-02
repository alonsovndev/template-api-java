package com.alonsovndev.template.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

class CorsTest {

  private static MockHttpServletRequestBuilder preflight(String origin) {
    return options("/health")
        .header("Origin", origin)
        .header("Access-Control-Request-Method", "GET");
  }

  @Nested
  @SpringBootTest(properties = "app.cors.allowed-origins=https://app.example.com")
  @AutoConfigureMockMvc
  @ActiveProfiles("test")
  class Configured {
    @Autowired MockMvc mockMvc;

    @Test
    void allowsConfiguredOriginWithoutCredentials() throws Exception {
      mockMvc
          .perform(preflight("https://app.example.com"))
          .andExpect(status().isOk())
          .andExpect(header().string("Access-Control-Allow-Origin", "https://app.example.com"))
          .andExpect(header().doesNotExist("Access-Control-Allow-Credentials"));
    }

    @Test
    void rejectsOtherOrigins() throws Exception {
      mockMvc.perform(preflight("https://evil.example.com")).andExpect(status().isForbidden());
    }
  }

  @Nested
  @SpringBootTest
  @AutoConfigureMockMvc
  @ActiveProfiles("test")
  class Disabled {
    @Autowired MockMvc mockMvc;

    @Test
    void sendsNoCorsHeadersByDefault() throws Exception {
      mockMvc
          .perform(preflight("https://app.example.com"))
          .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
  }
}
