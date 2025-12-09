package com.concessions.model;

import java.util.stream.Stream;

public enum StatusType {
  NEW("New"),
  OPEN("Open"),
  SUSPEND("Suspend"),
  CLOSE("Close"),
  SYNC("Sync");

  private final String name;

  private StatusType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  // A static lookup method to find the enum by its name
  public static StatusType fromValue(String name) {
    return Stream.of(StatusType.values())
        .filter(type -> type.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: " + name));
  }
}