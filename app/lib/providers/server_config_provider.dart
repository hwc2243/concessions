import 'package:flutter/material.dart';
import '/core/utils/device_utils.dart';
import '/models/dto/device_registration_request.dart';
import '/models/dto/device_registration_response.dart';
import '/network/discovery_service.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import 'client_config_provider.dart';

class ServerConfigProvider extends ChangeNotifier {
  String? _serverIp;
  String? get serverIp => _serverIp;

  int? _serverPort;
  int? get serverPort => _serverPort;

  bool _isSearching = false;
  bool get isSearching => _isSearching;

  bool _isConnected = false;
  bool get isConnected => _isConnected;

  bool _isRegistering = false;
  bool get isRegistering => _isRegistering;

  /// Runs the UDP discovery and updates global server state
  Future<bool> discoverServer() async {
    _isSearching = true;
    _isConnected = false;
    notifyListeners();

    final result = await DiscoveryService().findServer();

    if (result != null) {
      _serverIp = result['serverIp'];
      _serverPort = result['serverPort'];
      _isConnected = true;
      _isSearching = false;
      notifyListeners();
      return true;
    }

    _isSearching = false;
    notifyListeners();
    return false;
  }

  Future<void> registerWithServer({
    required String pin,
    required String deviceType,
    required ClientConfigProvider clientConfig,
  }) async {
    if (serverIp == null) return;

    _isRegistering = true;
    notifyListeners();

    try {
      String deviceId = await DeviceUtil.getUniqueId();

      final request = DeviceRegistrationRequestDTO(
        pin: pin,
        deviceId: deviceId,
        deviceType: deviceType,
        deviceIp: clientConfig.deviceIp ?? "0.0.0.0",
        devicePort: clientConfig.devicePort ?? 0,
      );

      final response =
          await MessengerService.sendRequest<DeviceRegistrationResponseDTO>(
            serverIp: serverIp!,
            serverPort: serverPort!,
            service: NetworkConstants.deviceService,
            action: NetworkConstants.deviceRegisterAction,
            payload: request.toJson(),
            fromJson: (json) => DeviceRegistrationResponseDTO.fromJson(json),
          );

      if (response != null) {
        await clientConfig.setClientDeviceConfig(
          deviceId: deviceId, // The one we generated via DeviceUtil
          deviceNumber: response.deviceNumber,
        );
      }
    } catch (e) {
      debugPrint("Registration failed: $e");
      rethrow;
    } finally {
      _isRegistering = false;
      notifyListeners();
    }
  }

  /// Manually clear connection (e.g., on logout/reset)
  void disconnect() {
    _serverIp = null;
    _serverPort = null;
    _isConnected = false;
    notifyListeners();
  }
}
