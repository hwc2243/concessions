import '/utils/date_extensions.dart';
import 'order_item_dto.dart';

class OrderDTO {
  final String? id;
  final String? journalId;
  final int menuId;
  final double orderTotal;
  final DateTime startTs;
  final DateTime? endTs;
  final List<OrderItemDTO> orderItems;

  OrderDTO({
    this.id,
    this.journalId,
    required this.menuId,
    required this.orderTotal,
    required this.startTs,
    this.endTs,
    required this.orderItems,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'journalId': journalId,
    'menuId': menuId,
    'orderTotal': orderTotal,
    'startTs': startTs?.toJavaSafeIsoString(),
    'endTs': endTs?.toJavaSafeIsoString(),
    'orderItems': orderItems.map((item) => item.toJson()).toList(),
  };

  factory OrderDTO.fromJson(Map<String, dynamic> json) {
    return OrderDTO(
      id: json['id']?.toString(),
      journalId: json['journalId']?.toString(),
      menuId: json['menuId'] as int? ?? 0,
      orderTotal: (json['orderTotal'] as num?)?.toDouble() ?? 0.0,
      startTs: _parseDateTime(json['startTs']) ?? DateTime.now(),
      endTs: _parseDateTime(json['endTs']),
      orderItems: json['orderItems'] != null
          ? (json['orderItems'] as List)
                .map((item) => OrderItemDTO.fromJson(item))
                .toList()
          : [],
    );
  }

  // Helper to handle both Long (milliseconds) from Java and ISO8601 Strings
  static DateTime? _parseDateTime(dynamic value) {
  if (value == null) return null;
  
  // Handle Java LocalDateTime list: [yr, mo, day, hr, min, sec, nano]
  if (value is List && value.length >= 6) {
    try {
      return DateTime(
        value[0], // Year
        value[1], // Month
        value[2], // Day
        value[3], // Hour
        value[4], // Minute
        value[5], // Second
      );
    } catch (e) {
      return null;
    }
  }
  
  if (value is int) return DateTime.fromMillisecondsSinceEpoch(value).toLocal();
  if (value is String) return DateTime.tryParse(value)?.toLocal();
  
  return null;
}
}
