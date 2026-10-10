package com.alonsovndev.template.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

class ProfileFilesTest {

  private static String read(String name) throws IOException {
    return new ClassPathResource(name).getContentAsString(StandardCharsets.UTF_8);
  }

  @Test
  void prodNeverImportsDotEnv() throws IOException {
    assertThat(read("application-prod.yml")).doesNotContain(".env").doesNotContain("import");
  }

  @Test
  void testProfileIsHermeticAndNeverImportsDotEnv() {
    assertThat(new ClassPathResource("application-test.yml").exists()).isFalse();
  }
}
