import 'dart:convert';
import '/models/dto/order_dto.dart';
import '/models/dto/simple_response.dart';
import '/network/network_constants.dart';
import '/providers/kitchen_provider.dart';
import 'base_handler.dart';

class OrderClientHandler extends BaseHandler {
  final KitchenProvider kitchenProvider;

  OrderClientHandler(this.kitchenProvider);

  @override
  String get name => NetworkConstants.orderService;

  @override
  Future<dynamic> process(String action, String payload) async {
    switch(action) {
      case NetworkConstants.orderCompletedAction:
        final order = OrderDTO.fromJson(jsonDecode(payload));
        if (order.id != null) {
          kitchenProvider.removeOrder(order.id!);
        }
        break;
      case NetworkConstants.orderCreatedAction:
        final order = OrderDTO.fromJson(jsonDecode(payload));
        kitchenProvider.addOrder(order);
        break;
      default:
        throw Exception("Action $action not implemented");
    }
    return SimpleResponseDTO(message: "OK");
  }
}