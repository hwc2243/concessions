abstract class BaseHandler {
  String get name;
  Future<dynamic> process(String action, String payload);
}