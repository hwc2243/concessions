import 'package:flutter/material.dart';
import '/models/dto/menu_dto.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import 'app_config_provider.dart';
import 'client_config_provider.dart';
import 'security_provider.dart';
import 'server_config_provider.dart';

class MenuProvider extends ChangeNotifier {
  MenuDTO? _menu;
  MenuDTO? get menu => _menu;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  Future<void> loadMenu({
    required ServerConfigProvider serverConfig,
    required ClientConfigProvider clientConfig,
    required SecurityProvider security,
  }) async {
    if (_isLoading) return;

    _isLoading = true;
    notifyListeners();

    try {
      final response = await MessengerService.sendRequest<MenuDTO>(
        serverIp: serverConfig.serverIp!,
        serverPort: serverConfig.serverPort!,
        service: NetworkConstants.menuService,
        action: NetworkConstants.menuGetAction,
        payload: {"pin": security.systemPin, "deviceId": clientConfig.deviceId},
        fromJson: (json) => MenuDTO.fromJson(json),
      );
      _menu = response;
    } catch (e) {
      rethrow;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void clear() {
    _menu = null;
    notifyListeners();
  }
}
