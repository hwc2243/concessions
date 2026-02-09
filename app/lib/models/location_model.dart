class Location {
  final int id;
  final String name;
  final int organizationId;

  Location({
    required this.id, 
    required this.name, 
    required this.organizationId
  });

  factory Location.fromJson(Map<String, dynamic> json) {
    return Location(
      id: json['id'] as int,
      name: json['name'] as String,
      organizationId: json['organizationId'] as int,
    );
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Location && runtimeType == other.runtimeType && id == other.id;

  @override
  int get hashCode => id.hashCode;
}