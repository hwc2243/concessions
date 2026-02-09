enum StatusType {
  NEW("New"),
  OPEN("Open"),
  SUSPEND("Suspend"),
  CLOSE("Close"),
  SYNC("Sync");

  final String label;
  const StatusType(this.label);
}

class JournalDTO {
  final String? id;
  final StatusType status;
  final int? menuId;
  final int orderCount;
  final double salesTotal;
  final DateTime startTs;
  final DateTime? endTs;
  final DateTime? syncTs;
  final int organizationId;

  JournalDTO({
    this.id,
    required this.status,
    this.menuId,
    required this.orderCount,
    required this.salesTotal,
    required this.startTs,
    this.endTs,
    this.syncTs,
    required this.organizationId,
  });

  factory JournalDTO.fromJson(Map<String, dynamic> json) {
    return JournalDTO(
      id: json['id']?.toString(),
      status: _parseStatus(json['status']),
      menuId: json['menuId'],
      orderCount: json['orderCount'] ?? 0,
      salesTotal: (json['salesTotal'] as num?)?.toDouble() ?? 0.0,
      startTs: _parseJavaDateTime(json['startTs']),
      endTs: json['endTs'] != null ? _parseJavaDateTime(json['endTs']) : null,
      syncTs: json['syncTs'] != null ? _parseJavaDateTime(json['syncTs']) : null,
      organizationId: json['organizationId'],
    );
  }

  // Helper to handle the [Year, Month, Day, Hour, Minute, Second, Nano] format
  static DateTime _parseJavaDateTime(dynamic value) {
    if (value is List) {
      return DateTime(
        value[0], // Year
        value[1], // Month
        value[2], // Day
        value.length > 3 ? value[3] : 0, // Hour
        value.length > 4 ? value[4] : 0, // Minute
        value.length > 5 ? value[5] : 0, // Second
      );
    }
    if (value is String) {
      return DateTime.parse(value);
    }
    return DateTime.now();
  }

  static StatusType _parseStatus(String? status) {
    return StatusType.values.firstWhere(
      (e) => e.name == status || e.label == status,
      orElse: () => StatusType.NEW,
    );
  }
}