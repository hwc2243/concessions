import 'dart:convert';
import 'package:http/http.dart' as http;
import '/core/security/auth_service.dart';
import '/models/menu_model.dart';
import '/providers/app_config_provider.dart';

class MenuRestService {
  final AuthService _authService = AuthService();
  final AppConfigProvider _appConfigProvider; // Reference to the provider
  final String _baseUrl = 'http://10.0.2.2:8080'; 

  MenuRestService(this._appConfigProvider);

  Future<List<Menu>> fetchMenus() async {
    final token = await _authService.getAccessToken();
    final orgId = _appConfigProvider.tenantId?.toString();

    if (orgId == null) {
      throw Exception("No active organization selected in provider");
    }

    final response = await http.get(
      Uri.parse('$_baseUrl/api/external/menu'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
        'organizationId': orgId, 
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> body = jsonDecode(response.body);
      return body.map((item) => Menu.fromJson(item)).toList();
    } else {
      throw Exception("Failed to load menus: ${response.statusCode}");
    }
  }
}