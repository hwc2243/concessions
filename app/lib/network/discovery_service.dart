import 'dart:io';
import 'dart:convert';
import 'dart:async';

class DiscoveryService {
  static const int minPort = 9371;
  static const int maxPort = 9390;

  Future<Map<String, dynamic>?> findServer() async {
    // 1. Get the specific subnet broadcast (e.g., 192.168.1.255)
    String broadcastTarget = await getBroadcastAddress() ?? "255.255.255.255";
    print("Targeting broadcast address: $broadcastTarget");

    RawDatagramSocket socket = await RawDatagramSocket.bind(
      InternetAddress.anyIPv4,
      0,
    );
    socket.broadcastEnabled = true;

    // Give iOS a moment to bind the socket to the interface
    await Future.delayed(const Duration(milliseconds: 100));

    final Completer<Map<String, dynamic>?> completer = Completer();

    socket.listen((RawSocketEvent event) {
      if (event == RawSocketEvent.read) {
        Datagram? dg = socket.receive();
        if (dg != null) {
          String response = utf8.decode(dg.data).trim();
          if (response.startsWith("WELCOME:")) {
            final jsonStr = response.substring(8);
            if (!completer.isCompleted) completer.complete(jsonDecode(jsonStr));
            socket.close();
          }
        }
      }
    });

    for (int port = minPort; port <= maxPort; port++) {
      if (completer.isCompleted) break;

      try {
        socket.send(
          utf8.encode("HELLO"),
          InternetAddress(broadcastTarget),
          port,
        );
      } catch (e) {
        // If Errno 65 happens here, iOS is still blocking the route
        print("Failed to send to $broadcastTarget on port $port: $e");
      }

      await Future.delayed(const Duration(milliseconds: 50));
    }

    return completer.future.timeout(
      const Duration(seconds: 5),
      onTimeout: () {
        socket.close();
        return null;
      },
    );
  }

  Future<String?> getBroadcastAddress() async {
    try {
      // Get all network interfaces (Wi-Fi, Cellular, etc.)
      for (var interface in await NetworkInterface.list()) {
        for (var addr in interface.addresses) {
          // Look for a standard local IPv4 address (e.g., 192.168.x.x or 10.x.x.x)
          if (addr.type == InternetAddressType.IPv4 && !addr.isLoopback) {
            final parts = addr.address.split('.');
            if (parts.length == 4) {
              // Create the broadcast address by forcing the last part to 255
              return "${parts[0]}.${parts[1]}.${parts[2]}.255";
            }
          }
        }
      }
    } catch (e) {
      print("Could not determine subnet: $e");
    }
    return null;
  }
}
