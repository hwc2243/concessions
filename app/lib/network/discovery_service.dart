import 'dart:io';
import 'dart:convert';
import 'dart:async';

class DiscoveryService {
  static const int minPort = 9371;
  static const int maxPort = 9390;

  Future<Map<String, dynamic>?> findServer() async {
    // Bind to any available local port for receiving the response
    RawDatagramSocket socket = await RawDatagramSocket.bind(InternetAddress.anyIPv4, 0);
    socket.broadcastEnabled = true;
    
    final Completer<Map<String, dynamic>?> completer = Completer();
    
    // Listen for incoming Datagrams
    socket.listen((RawSocketEvent event) {
      if (event == RawSocketEvent.read) {
        Datagram? dg = socket.receive();
        if (dg != null) {
          String response = utf8.decode(dg.data).trim();
          if (response.startsWith("WELCOME:")) {
            try {
              final jsonStr = response.substring(8);
              completer.complete(jsonDecode(jsonStr));
              socket.close();
            } catch (e) {
              print("Error parsing Welcome JSON: $e");
            }
          }
        }
      }
    });

    // Send "HELLO" to every port in the range
    for (int port = minPort; port <= maxPort; port++) {
      if (completer.isCompleted) break;
      
      socket.send(
        utf8.encode("HELLO"),
        InternetAddress("255.255.255.255"),
        port,
      );
      
      // Small delay between port blasts to avoid flooding and give sockets time to breathe
      await Future.delayed(const Duration(milliseconds: 50));
    }

    // Return the result or timeout after 5 seconds of total searching
    return completer.future.timeout(
      const Duration(seconds: 5),
      onTimeout: () {
        socket.close();
        return null;
      },
    );
  }
}