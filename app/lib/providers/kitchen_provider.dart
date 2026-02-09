import 'package:flutter/material.dart';
import '/models/dto/order_dto.dart';
import '/models/dto/order_queue_get_all_response.dart';
import '/models/dto/order_request.dart';
import '/models/dto/simple_response.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import '/providers/client_config_provider.dart';
import '/providers/server_config_provider.dart';
import '/providers/security_provider.dart';

class KitchenProvider extends ChangeNotifier {
  List<OrderDTO> _activeOrders = [];
  List<OrderDTO> get activeOrders => _activeOrders;

  Future<void> loadInitialOrders({
    required ServerConfigProvider serverConfig,
    required ClientConfigProvider clientConfig,
    required SecurityProvider security,
  }) async {
    try {
      // 1. Create the request DTO (Matching your Java SimpleDeviceRequestDTO)
      final request = {
        'pin': security.systemPin ?? "",
        'deviceId': clientConfig.deviceId ?? "",
      };

      // 2. Send the request via your MessengerService
      final response =
          await MessengerService.sendRequest<OrderQueueGetAllResponseDTO>(
            serverIp: serverConfig.serverIp!,
            serverPort: serverConfig.serverPort!,
            service: NetworkConstants.orderService,
            action: NetworkConstants.orderGetallAction,
            payload: request,
            fromJson: (json) => OrderQueueGetAllResponseDTO.fromJson(json),
          );

      if (response != null && response.orders != null) {
        _activeOrders = response.orders!;
        notifyListeners();
      }
    } catch (e) {
      debugPrint("Failed to load kitchen orders: $e");
      // You might want to handle this error in the UI
    }
  }

  // Called when the initial list is fetched 
  void updateOrders(List<OrderDTO> newOrders) {
    _activeOrders = newOrders;
    notifyListeners();
  }

  void addOrder(OrderDTO order) {
    bool alreadyExists = _activeOrders.any((o) => o.id == order.id);
    if (!alreadyExists) {
      _activeOrders.add(order);
      notifyListeners();
    }
  }

  Future<bool> completeOrder({
    required OrderDTO order,
    required ServerConfigProvider serverConfig,
    required ClientConfigProvider clientConfig,
    required SecurityProvider security,
  }) async {
    try {
      final request = OrderRequestDTO(
        pin: security.systemPin ?? "",
        deviceId: clientConfig.deviceId ?? "",
        order: order,
      );

      final response = await MessengerService.sendRequest<SimpleResponseDTO>(
        serverIp: serverConfig.serverIp!,
        serverPort: serverConfig.serverPort!,
        service: NetworkConstants.orderService,
        action: NetworkConstants.orderCompleteAction,
        payload: request.toJson(),
        fromJson: (json) => SimpleResponseDTO.fromJson(json),
      );

      if (response != null && response.isSuccess) {
        _activeOrders.removeWhere((o) => o.id == order.id);
        notifyListeners();
        return true;
      }
      return false;
    } catch (e) {
      return false;
    }
  }

  void removeOrder(String orderId) {
    _activeOrders.removeWhere((o) => o.id == orderId);
    notifyListeners();
  }

  int get totalItemCount => _activeOrders.fold<int>(0, (int sum, order) {
    return (sum + (order.orderItems?.length ?? 0)).toInt();
  });
}
