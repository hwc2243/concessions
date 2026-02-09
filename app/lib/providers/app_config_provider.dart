import 'package:flutter/material.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '/core/security/auth_service.dart';
import '/models/device_type.dart';
import '/models/organization_model.dart';
import '/models/dto/configuration_response.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import 'client_config_provider.dart';
import 'server_config_provider.dart';
import 'security_provider.dart';

enum POSRole { server, client, undecided }

class AppConfigProvider extends ChangeNotifier {
  final AuthService _authService = AuthService();

  // Keys for SharedPreferences
  static const String _keyRole = 'pos_role';
  static const String _keyOrgId = 'active_org_id';
  static const String _keyLocId = 'active_loc_id';
  static const String _keyMenuId = 'active_menu_id';

  // if the application is authenticating against OIDC
  bool _isAuthenticating = false;
  bool get isAuthenticating => _isAuthenticating;

  // the role of this system
  POSRole _role = POSRole.undecided;
  POSRole get role => _role;

  DeviceTypeType? _deviceType;
  DeviceTypeType? get deviceType => _deviceType;

  bool _isConfigured = false;
  bool get isConfigured => _isConfigured;

  // if there is an internet connection
  bool _hasInternet = true;
  bool get hasInternet => _hasInternet;

  // Configuration IDs (The "Metadata")
  int? _organizationId;
  int? get organizationId => _organizationId;
  String? _organizationName;
  String? get organizationName => _organizationName;

  int? _locationId;
  int? get locationId => _locationId;
  String? _locationName;
  String? get locationName => _locationName;

  int? _menuId;
  int? get menuId => _menuId;
  String? _menuName;
  String? get menuName => _menuName;

  bool _isChecking = false;
  bool get isChecking => _isChecking;

  // this is used for multi tenancy and should be moved to another class
  Organization? _activeOrganization;
  Organization? get activeOrganization => _activeOrganization;

  AppConfigProvider() {
    _initialize();
  }

  Future<void> _initialize() async {
    await checkConnectivity();
    await _loadConfiguration();

    // Listen for changes in real-time
    Connectivity().onConnectivityChanged.listen((
      List<ConnectivityResult> results,
    ) {
      _updateConnectionStatus(results);
    });
  }

  /// Loads saved setup configuration from disk
  Future<void> _loadConfiguration() async {
    final prefs = await SharedPreferences.getInstance();

    // Load Role
    final savedRoleIndex = prefs.getInt(_keyRole);
    if (savedRoleIndex != null) {
      _role = POSRole.values[savedRoleIndex];
    }

    _deviceType = prefs.containsKey('deviceType')
        ? DeviceTypeType.values.firstWhere(
            (e) => e.value == prefs.getString('deviceType'),
          )
        : null;
    _isConfigured = prefs.getBool('isConfigured') ?? false;

    // Load IDs
    _organizationId = prefs.getInt(_keyOrgId);
    _locationId = prefs.getInt(_keyLocId);
    _menuId = prefs.getInt(_keyMenuId);

    // Note: _activeOrganization remains null until a fresh API call
    // or local DB fetch populates it based on _selectedOrgId.

    notifyListeners();
  }

  Future<void> saveClientConfiguration(
    POSRole role,
    DeviceTypeType type,
  ) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setInt(_keyRole, role.index);
    await prefs.setString('deviceType', type.value);
    await prefs.setBool('isConfigured', true);

    _role = role;
    _deviceType = type;
    _isConfigured = true;

    notifyListeners();
  }

  /// Finalizes the setup and persists everything to disk
  Future<void> saveServerConfiguration({
    required int orgId,
    required int locationId,
    required int menuId,
  }) async {
    final prefs = await SharedPreferences.getInstance();

    await prefs.setInt(_keyOrgId, orgId);
    await prefs.setInt(_keyLocId, locationId);
    await prefs.setInt(_keyMenuId, menuId);
    await prefs.setInt(_keyRole, _role.index);

    _organizationId = orgId;
    _locationId = locationId;
    _menuId = menuId;

    notifyListeners();
  }

  bool get isConfigurationComplete =>
      _organizationId != null &&
      _locationId != null &&
      _menuId != null;

  Future<void> checkConnectivity() async {
    final results = await Connectivity().checkConnectivity();
    _updateConnectionStatus(results);
  }

  void _updateConnectionStatus(List<ConnectivityResult> results) {
    // Basic check: if any result is not 'none', we assume some level of internet
    _hasInternet = !results.contains(ConnectivityResult.none);
    notifyListeners();
  }

  void setClientRole() {
    _role = POSRole.client;
    notifyListeners();
  }

  void setRole(POSRole newRole) {
    _role = newRole;
    notifyListeners();
  }

  Future<void> resetConfiguration() async {
    final prefs = await SharedPreferences.getInstance();

    // Clear all setup-related keys
    await prefs.remove(_keyRole);
    await prefs.remove(_keyOrgId);
    await prefs.remove(_keyLocId);
    await prefs.remove(_keyMenuId);
    await prefs.remove('deviceType');
    await prefs.remove('isConfigured');

    // Reset local state
    _role = POSRole.undecided;
    _deviceType = null;
    _isConfigured = false;
    _organizationId = null;
    _organizationName = null;
    _locationId = null;
    _locationName = null;
    _menuId = null;
    _menuName = null;

    notifyListeners();
  }

  Future<void> loginAndSetServer() async {
    _isAuthenticating = true;
    notifyListeners();

    bool success = await _authService.login();

    if (success) {
      _role = POSRole.server;
      // After login, we would proceed to Location Selection
    } else {
      // Handle login failure (e.g., user canceled)
    }

    _isAuthenticating = false;
    notifyListeners();
  }

  void setActiveOrganization(Organization? org) {
    _activeOrganization = org;
    _organizationId = org?.id;
    notifyListeners();
  }

Future<void> loadLocationConfiguration({
    required ServerConfigProvider serverConfig,
    required ClientConfigProvider clientConfig,
    required SecurityProvider security,
  }) async {
    try {
      final response = await MessengerService.sendRequest<ConfigurationResponseDTO>(
        serverIp: serverConfig.serverIp!,
        serverPort: serverConfig.serverPort!,
        service: NetworkConstants.configurationService,
        action: NetworkConstants.configurationLocationAction,
        payload: {
          "pin": security.systemPin,
          "deviceId": clientConfig.deviceId,
        },
        fromJson: (json) => ConfigurationResponseDTO.fromJson(json),
      );

      // Store the configuration globally
      _organizationId = response?.organizationId;
      _organizationName = response?.organizationName;
      _locationId = response?.locationId;
      _locationName = response?.locationName;
      _menuId = response?.menuId;
      _menuName = response?.menuName;

      notifyListeners();
    } catch (e) {
      debugPrint("Failed to load location config: $e");
      rethrow;
    }
  }

  String? get tenantId => _organizationId?.toString();
}
