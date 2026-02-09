import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import '/network/handlers/handler_registry.dart';

class ClientNetworkListener {
  ServerSocket? _serverSocket;
  final HandlerRegistry _registry;

  ClientNetworkListener(this._registry);

  Future<void> start() async {
    try {
      _serverSocket = await ServerSocket.bind(InternetAddress.anyIPv4, 0);
      _serverSocket!.listen((Socket client) {
        _handleConnection(client);
      });
    } catch (e) {
      debugPrint("Failed to start Local Listener: $e");
    }
  }

  void _handleConnection(Socket socket) {
    debugPrint("Accepted connection from ${socket.remoteAddress.address}");

    // Use utf8.decoder on the socket stream, then split by lines
    utf8.decoder
        .bind(socket)
        .transform(const LineSplitter())
        .listen(
          (String line) async {
            try {
              // Format: SERVICE|ACTION|PAYLOAD
              List<String> parts = line.split('|');
              if (parts.length < 3) return;

              String service = parts[0].trim();
              String action = parts[1].trim();
              String payload = parts[2].trim();

              // Delegate to Handler Registry
              final response = await _registry.handleRequest(
                service,
                action,
                payload,
              );

              // Use write and add the newline manually to ensure Java's readLine() works
              socket.write("OK|${jsonEncode(response)}\n");
            } catch (e) {
              socket.write("ERROR|${jsonEncode({'message': e.toString()})}\n");
            } finally {
              await socket.flush();
              socket.close();
            }
          },
          onError: (err) {
            debugPrint("Socket error: $err");
            socket.close();
          },
          onDone: () => socket.close(),
        );
  }

  int? get port => _serverSocket?.port;

  void stop() {
    _serverSocket?.close();
  }
}
