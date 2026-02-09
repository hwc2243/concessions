import '/models/dto/order_dto.dart';

class OrderQueueGetAllResponseDTO {
  final List<OrderDTO>? orders;

  OrderQueueGetAllResponseDTO({this.orders});

  factory OrderQueueGetAllResponseDTO.fromJson(Map<String, dynamic> json) {
    return OrderQueueGetAllResponseDTO(
      orders: json['orders'] != null
          ? (json['orders'] as List).map((i) => OrderDTO.fromJson(i)).toList()
          : null,
    );
  }
}