package com.alonsovndev.template.architecture.fixtures.application;

import com.alonsovndev.template.architecture.fixtures.infrastructure.SomeAdapter;

/** Deliberate violation: application depending on infrastructure. */
public class UseCaseUsesInfrastructure {

  SomeAdapter violates;
}
