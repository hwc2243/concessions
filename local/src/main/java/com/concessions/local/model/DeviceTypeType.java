package com.concessions.local.model;

import java.util.stream.Stream;

public enum DeviceTypeType {
  SERVER("SERVER"),
  KITCHEN("KITCHEN"),
  POS("POS");

  private final String name;

  private DeviceTypeType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  // A static lookup method to find the enum by its name
  public static DeviceTypeType fromValue(String name) {
    return Stream.of(DeviceTypeType.values())
        .filter(type -> type.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: " + name));
  }
}