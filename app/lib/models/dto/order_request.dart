import '/models/dto/order_dto.dart';

class OrderRequestDTO {
  final String pin;
  final String deviceId;
  final OrderDTO order;

  OrderRequestDTO({
    required this.pin,
    required this.deviceId,
    required this.order,
  });

  Map<String, dynamic> toJson() => {
        'pin': pin,
        'deviceId': deviceId,
        'order': order.toJson(),
      };
}