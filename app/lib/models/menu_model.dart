import 'menu_item_model.dart';

class Menu {
  final int id;
  final String name;
  final String? description;
  final int organizationId;
  final List<MenuItem> menuItems;

  Menu({
    required this.id,
    required this.name,
    this.description,
    required this.organizationId,
    required this.menuItems});

  factory Menu.fromJson(Map<String, dynamic> json) {
    return Menu(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      organizationId: json['organizationId'],
      menuItems: (json['menuItems'] as List)
          .map((item) => MenuItem.fromJson(item))
          .toList(),
    );
  }
}