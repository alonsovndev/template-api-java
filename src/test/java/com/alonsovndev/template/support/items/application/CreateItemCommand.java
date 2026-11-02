package com.alonsovndev.template.support.items.application;

import java.util.UUID;

public record CreateItemCommand(UUID identifier, String name) {}
