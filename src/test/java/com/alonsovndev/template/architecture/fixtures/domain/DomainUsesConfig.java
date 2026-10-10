package com.alonsovndev.template.architecture.fixtures.domain;

import com.alonsovndev.template.config.AppProperties;

/** Deliberate violation: domain reaching into startup configuration. */
public class DomainUsesConfig {

  AppProperties violates;
}
