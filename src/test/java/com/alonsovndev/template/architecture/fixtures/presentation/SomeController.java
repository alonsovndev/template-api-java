package com.alonsovndev.template.architecture.fixtures.presentation;

import com.alonsovndev.template.architecture.fixtures.domain.DomainUsesSpring;

/** Deliberate violation: presentation depending on domain. */
public class SomeController {

  DomainUsesSpring violates;
}
