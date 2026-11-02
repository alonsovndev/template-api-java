package com.alonsovndev.template.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootTest
@ActiveProfiles("test")
class RoutesTest {

  @Autowired private RequestMappingHandlerMapping handlerMapping;

  @Test
  void registersOnlyTheHealthRoutes() {
    Set<String> routes =
        handlerMapping.getHandlerMethods().entrySet().stream()
            .filter(entry -> isApplicationHandler(entry.getValue()))
            .flatMap(entry -> entry.getKey().getPathPatternsCondition().getPatternValues().stream())
            .collect(Collectors.toSet());

    assertThat(routes).containsExactlyInAnyOrder("/health", "/health/ready");
  }

  private static boolean isApplicationHandler(HandlerMethod handler) {
    return handler.getBeanType().getName().startsWith("com.alonsovndev.template");
  }
}
