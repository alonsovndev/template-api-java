package com.alonsovndev.template.architecture.fixtures.domain;

/** Deliberate violation used to prove the architecture rules reject framework use in domain. */
public class DomainUsesSpring {

  boolean violates() {
    return org.springframework.util.StringUtils.hasText("x");
  }
}
