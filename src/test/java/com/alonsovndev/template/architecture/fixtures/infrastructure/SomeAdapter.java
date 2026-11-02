package com.alonsovndev.template.architecture.fixtures.infrastructure;

import com.alonsovndev.template.architecture.fixtures.presentation.SomeController;

/** Deliberate violation: infrastructure depending on presentation. */
public class SomeAdapter {

  SomeController violates;
}
