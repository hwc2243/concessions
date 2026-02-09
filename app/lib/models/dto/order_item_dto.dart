class OrderItemDTO {
  final String? id;
  final int menuItemId;
  final String name;
  final double price;

  OrderItemDTO({
    this.id,
    required this.menuItemId,
    required this.name,
    required this.price,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'menuItemId': menuItemId,
    'name': name,
    'price': price,
  };

  factory OrderItemDTO.fromJson(Map<String, dynamic> json) {
    return OrderItemDTO(
      id: json['id']?.toString(),
      menuItemId: json['menuItemId'] as int? ?? 0,
      name: json['name']?.toString() ?? "Unknown Item",
      price: (json['price'] as num?)?.toDouble() ?? 0.0,
    );
  }
}