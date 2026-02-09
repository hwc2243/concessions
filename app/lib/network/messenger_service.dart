import 'dart:convert';
import 'dart:io';
import 'dart:async';
import 'package:flutter/foundation.dart';

class MessengerException implements Exception {
  final String message;
  MessengerException(this.message);
  @override
  String toString() => "MessengerException: $message";
}

class MessengerService {
  /// Sends a request to the Java server and parses the response
  /// [service]: The Java service name
  /// [action]: The specific method/action
  /// [payload]: The object to be serialized to JSON
  /// [fromJson]: A function that takes a Map and returns the desired DTO
  static Future<T?> sendRequest<T>({
    required String serverIp,
    required int serverPort,
    required String service,
    required String action,
    required Object payload,
    required T Function(Map<String, dynamic>) fromJson,
  }) async {
    Socket? socket;
    try {
      // 1. Serialize the message (Format: SERVICE|ACTION|JSON)
      String jsonPayload = jsonEncode(payload);
      String message = "$service|$action|$jsonPayload\n"; // \n is vital for Java's readLine()

      // 2. Connect with a timeout
      socket = await Socket.connect(serverIp, serverPort, timeout: const Duration(seconds: 5));

      // 3. Send the message
      socket.write(message);
      await socket.flush();

      // 4. Read the response
      // We use a Completer to turn the socket stream into a single response
      final completer = Completer<String>();
      socket.listen(
        (List<int> data) {
          completer.complete(utf8.decode(data));
        },
        onError: (error) => completer.completeError(error),
        onDone: () {
          if (!completer.isCompleted) completer.completeError("Server closed connection");
        },
      );

      String rawResponse = await completer.future;
      return _parseResponse<T>(rawResponse, fromJson);

    } on SocketException catch (e) {
      throw MessengerException("Could not connect to server: ${e.message}");
    } on TimeoutException {
      throw MessengerException("Connection to server timed out");
    } catch (e) {
      throw MessengerException("Communication error: $e");
    } finally {
      socket?.destroy(); // Always close the socket
    }
  }

  static T? _parseResponse<T>(String raw, T Function(Map<String, dynamic>) fromJson) {
    if (raw.isEmpty) throw MessengerException("Empty response from server");

    // Expecting format: STATUS|PAYLOAD
    int separatorIndex = raw.indexOf('|');
    if (separatorIndex == -1) throw MessengerException("Invalid response format: $raw");

    String status = raw.substring(0, separatorIndex).trim();
    String payloadJson = raw.substring(separatorIndex + 1).trim();

    if (status.toUpperCase() == "OK") {
      if (payloadJson.isEmpty) return null;
      return fromJson(jsonDecode(payloadJson));
    } else {
      // Logic for SimpleResponseDTO (Errors)
      Map<String, dynamic> errorMap = jsonDecode(payloadJson);
      throw MessengerException(errorMap['message'] ?? "Unknown server error");
    }
  }
}