enum CategoryType {
  APPETIZER("APPETIZER", "Appetizer"),
  ENTREE("ENTREE", "Entree"),
  SIDE("SIDE", "Side"),
  DRINK("DRINK", "Drink"),
  DESSERT("DESSERT", "Dessert");

  // The backend key (usually the enum name) and the user-facing label
  final String jsonKey;
  final String label;

  const CategoryType(this.jsonKey, this.label);

  /// Helper to convert backend String to Dart Enum
  static CategoryType fromJson(String key) {
    return CategoryType.values.firstWhere(
      (e) => e.jsonKey == key,
      orElse: () => CategoryType.ENTREE, // Default fallback
    );
  }
}