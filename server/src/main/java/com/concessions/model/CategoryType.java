package com.concessions.model;

import java.util.stream.Stream;

public enum CategoryType {
  APPETIZER("Appetizer"),
  ENTREE("Entree"),
  SIDE("Side"),
  DRINK("Drink"),
  DESSERT("Dessert");

  private final String name;

  private CategoryType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  // A static lookup method to find the enum by its name
  public static CategoryType fromValue(String name) {
    return Stream.of(CategoryType.values())
        .filter(type -> type.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown enum value: " + name));
  }
}