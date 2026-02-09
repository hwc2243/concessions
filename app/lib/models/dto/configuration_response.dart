class ConfigurationResponseDTO {
  final int organizationId;
  final String organizationName;
  final int locationId;
  final String locationName;
  final int menuId;
  final String menuName;

  ConfigurationResponseDTO({
    required this.organizationId,
    required this.organizationName,
    required this.locationId,
    required this.locationName,
    required this.menuId,
    required this.menuName,
  });

  factory ConfigurationResponseDTO.fromJson(Map<String, dynamic> json) {
    return ConfigurationResponseDTO(
      organizationId: json['organizationId'],
      organizationName: json['organizationName'] ?? "",
      locationId: json['locationId'],
      locationName: json['locationName'] ?? "",
      menuId: json['menuId'],
      menuName: json['menuName'] ?? "",
    );
  }
}