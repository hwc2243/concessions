import 'dart:convert';
import 'package:http/http.dart' as http;
import '../core/security/auth_service.dart';
import '../models/location_model.dart';
import '../providers/app_config_provider.dart';

class LocationRestService {
  final AuthService _authService = AuthService();
  final AppConfigProvider _appConfigProvider; // Reference to the provider
  final String _baseUrl = 'http://10.0.2.2:8080'; 

  LocationRestService(this._appConfigProvider);

  Future<List<Location>> fetchLocations () async {
    final token = await _authService.getAccessToken();
    final orgId = _appConfigProvider.tenantId?.toString();

    if (orgId == null) {
      throw Exception("No active organization selected in provider");
    }

    final response = await http.get(
      Uri.parse('$_baseUrl/api/external/location'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
        'organizationId': orgId.toString(), // The multi-tenant header
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> body = jsonDecode(response.body);
      return body.map((item) => Location.fromJson(item)).toList();
    } else {
      throw Exception("Failed to load locations: ${response.statusCode}");
    }
  }
}