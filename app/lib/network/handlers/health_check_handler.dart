import '/network/network_constants.dart';
import '/models/dto/simple_response.dart';
import 'base_handler.dart';

class HealthCheckHandler extends BaseHandler {
  @override
  String get name => NetworkConstants.healthService;

  @override
  Future<dynamic> process(String action, String payload) async {
    switch (action) {
      case NetworkConstants.healthCheckAction:
        return SimpleResponseDTO(message: "ALIVE");
      default:
        throw Exception("Action $action not implemented");
    }
  }
}