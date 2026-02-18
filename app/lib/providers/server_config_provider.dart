import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '/core/utils/device_utils.dart';
import '/models/dto/device_registration_request.dart';
import '/models/dto/device_registration_response.dart';
import '/network/discovery_service.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import 'client_config_provider.dart';

class ServerConfigProvider extends ChangeNotifier {
  static const String _keyIp = 'server_ip';
  static const String _keyPort = 'server_port';

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

  Future<void> loadConfig() async {
    final prefs = await SharedPreferences.getInstance();
    _serverIp = prefs.getString(_keyIp);
    _serverPort = prefs.getInt(_keyPort);

    if (_serverIp != null && _serverPort != null) {
      _isConnected = true;
      debugPrint("Loaded saved server config: $_serverIp:$_serverPort");
    }
    notifyListeners();
  }

  /// Private helper to persist data
  Future<void> _persistConfig(String ip, int port) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_keyIp, ip);
    await prefs.setInt(_keyPort, port);
  }

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

      await _persistConfig(_serverIp!, _serverPort!);

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
  void disconnect() async {
    _serverIp = null;
    _serverPort = null;
    _isConnected = false;

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_keyIp);
    await prefs.remove(_keyPort);

    notifyListeners();
  }

  Future<void> saveManualConfig({
    required String serverIp,
    required int serverPort,
  }) async {
    _serverIp = serverIp;
    _serverPort = serverPort;
    _isConnected = true;
    _isSearching = false;

    await _persistConfig(_serverIp!, _serverPort!);

    notifyListeners();
  }
}
