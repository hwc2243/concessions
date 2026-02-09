import 'dart:typed_data';

class MenuItemDTO {
  final int id;
  final String name;
  final String? description;
  final String category; // Matches CategoryType enum/string
  final double price;
  final int organizationId;

  MenuItemDTO({
    required this.id,
    required this.name,
    this.description,
    required this.category,
    required this.price,
    required this.organizationId,
  });

  factory MenuItemDTO.fromJson(Map<String, dynamic> json) {
    return MenuItemDTO(
      id: json['id'],
      name: json['name'] ?? '',
      description: json['description'],
      category: json['category'] ?? '',
      price: (json['price'] as num).toDouble(),
      organizationId: json['organizationId'],
    );
  }
}