// lib/providers/order_provider.dart
import 'package:flutter/material.dart';
import '/models/dto/menu_item_dto.dart';
import '/models/dto/order_request.dart';
import '/models/dto/order_dto.dart';
import '/models/dto/order_item_dto.dart';

class OrderProvider extends ChangeNotifier {
  final List<OrderItemDTO> _items = [];
  List<OrderItemDTO> get items => _items;

  int get itemCount => _items.length;

  double get totalPrice => _items.fold(0, (sum, item) => sum + item.price);

  DateTime? _startTs;

  void addItem(MenuItemDTO menuItem) {
    _startTs ??= DateTime.now();

    _items.add(
      OrderItemDTO(
        menuItemId: menuItem.id,
        name: menuItem.name,
        price: menuItem.price,
      ),
    );
    notifyListeners();
  }

  void removeItem(int index) {
    _items.removeAt(index);
    if (_items.isEmpty) _startTs = null;
    notifyListeners();
  }

  void clearOrder() {
    _items.clear();
    _startTs = null;
    notifyListeners();
  }

  OrderRequestDTO createOrderRequest({
    required String pin,
    required String deviceId,
    required int menuId,
    required String journalId,
  }) {
    final orderData = OrderDTO(
      menuId: menuId,
      journalId: journalId,
      orderItems: _items,
      orderTotal: totalPrice,
      startTs: _startTs ?? DateTime.now(),
    );

    return OrderRequestDTO(pin: pin, deviceId: deviceId, order: orderData);
  }
}
