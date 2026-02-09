import 'menu_item_dto.dart';

class MenuDTO {
  final int id;
  final String name;
  final String? description;
  final int organizationId;
  final List<MenuItemDTO> menuItems;

  MenuDTO({
    required this.id,
    required this.name,
    this.description,
    required this.organizationId,
    required this.menuItems,
  });

  factory MenuDTO.fromJson(Map<String, dynamic> json) {
    return MenuDTO(
      id: json['id'],
      name: json['name'] ?? '',
      description: json['description'],
      organizationId: json['organizationId'],
      menuItems: (json['menuItems'] as List)
          .map((i) => MenuItemDTO.fromJson(i))
          .toList(),
    );
  }
}