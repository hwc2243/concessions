import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/organization_model.dart';
import '../core/security/auth_service.dart';

class OrganizationRestService {
  final AuthService _authService = AuthService();
  final String _baseUrl = 'http://10.0.2.2:8080';

  Future<List<Organization>> fetchOrganizations() async {
    final token = await _authService.getAccessToken();
    
    if (token == null) throw Exception("Unauthorized: No access token found");

    final response = await http.get(
      Uri.parse('$_baseUrl/api/external/organization'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      List<dynamic> body = jsonDecode(response.body);
      return body.map((dynamic item) => Organization.fromJson(item)).toList();
    } else {
      throw Exception("Failed to load organizations: ${response.statusCode}");
    }
  }
}