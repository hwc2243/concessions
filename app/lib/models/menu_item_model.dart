import 'package:decimal/decimal.dart';
import 'category_type.dart';

class MenuItem {
  final int id;
  final String name;
  final String? description;
  final CategoryType category;
  final Decimal price; 
  final int organizationId;

  MenuItem({
    required this.id,
    required this.name,
    this.description,
    required this.category,
    required this.price,
    required this.organizationId});

  factory MenuItem.fromJson(Map<String, dynamic> json) {
    return MenuItem(
      id: json['id'],
      name: json['name'],
      description: json['description'] as String?,
      category: CategoryType.fromJson(json['category'] as String),
      price: Decimal.parse(json['price'].toString()),
      organizationId: json['organizationId'] as int,
    );
  }

  String get formattedPrice => price.toStringAsFixed(2);
}