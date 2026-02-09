import 'base_handler.dart';

class HandlerRegistry {
  final Map<String, BaseHandler> _handlers = {};

  void register(BaseHandler handler) {
    _handlers[handler.name] = handler;
  }

  Future<dynamic> handleRequest(String service, String action, String payload) async {
    final handler = _handlers[service];
    if (handler == null) throw Exception("Service $service not found");
    return handler.process(action, payload);
  }
}