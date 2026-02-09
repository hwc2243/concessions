import 'package:flutter/material.dart';
import 'package:network_info_plus/network_info_plus.dart';
import '/network/client_network_listener.dart';
import '/network/handlers/handler_registry.dart';
import '/network/handlers/health_check_handler.dart';
import '/network/handlers/journal_client_handler.dart';
import '/network/handlers/order_client_handler.dart';
import '/providers/journal_provider.dart';
import '/providers/kitchen_provider.dart';

class ClientConfigProvider extends ChangeNotifier {
  String? _deviceId;
  String? get deviceId => _deviceId;

  String? _deviceNumber;
  String? get deviceNumber => _deviceNumber;

  String? _deviceIp;
  String? get deviceIp => _deviceIp;

  int? _devicePort;
  int? get devicePort => _devicePort;

  ClientNetworkListener? _listener;

  Future<void> setClientDeviceConfig({
    required String deviceId,
    required String deviceNumber,
  }) async {
    _deviceId = deviceId;
    _deviceNumber = deviceNumber;

    notifyListeners();
  }

  Future<void> setClientNetworkConfig({
        String? deviceIp,
    int? devicePort,
  }) async {
    _deviceIp = deviceIp;
    _devicePort = devicePort;

    notifyListeners();
  }

  void clear() async {
    _deviceId = null;
    _deviceNumber = null;
    _deviceIp = null;
    _devicePort = null;

    notifyListeners();
  }

  Future<void> startListener(JournalProvider journalProvider, KitchenProvider kitchenProvider) async {
    final registry = HandlerRegistry();
    registry.register(HealthCheckHandler());
    registry.register(JournalClientHandler(journalProvider));
    registry.register(OrderClientHandler(kitchenProvider));

    _listener = ClientNetworkListener(registry);
    await _listener!.start();
    
    final info = NetworkInfo();
    _deviceIp = await info.getWifiIP();
    _devicePort = _listener!.port;
    
    notifyListeners();
  }

  // Call this in your clear/reset method
  void stopListener() {
    _listener?.stop();
  }
}
