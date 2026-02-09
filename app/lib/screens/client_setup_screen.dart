import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '/network/discovery_service.dart';
import '/providers/app_config_provider.dart';
import 'client_configuration_screen.dart';

class ClientSetupScreen extends StatefulWidget {
  const ClientSetupScreen({super.key});

  @override
  State<ClientSetupScreen> createState() => _ClientSetupScreenState();
}

class _ClientSetupScreenState extends State<ClientSetupScreen> {
  bool _isScanning = true;
  Map<String, dynamic>? _serverInfo;
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _startDiscovery();
  }

  /// Initiates the UDP port sweep (9371-9390) to find the Server
  Future<void> _startDiscovery() async {
    setState(() {
      _isScanning = true;
      _errorMessage = null;
      _serverInfo = null;
    });

    final result = await DiscoveryService().findServer();

    if (mounted) {
      setState(() {
        _isScanning = false;
        if (result != null) {
          _serverInfo = result;
        } else {
          _errorMessage =
              "Server not found. Ensure the Server is running and on the same Wi-Fi network.";
        }
      });
    }
  }

  /// Resets the role to undecided and returns to the SystemSetupScreen
  void _handleCancel() {
    context.read<AppConfigProvider>().setRole(POSRole.undecided);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Client Setup"),
        // Disable the default back button to force use of the Cancel button
        automaticallyImplyLeading: false, 
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // --- STATE: SCANNING ---
              if (_isScanning) ...[
                const CircularProgressIndicator(),
                const SizedBox(height: 24),
                const Text(
                  "Searching for Server...",
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
                ),
                const SizedBox(height: 8),
                const Text(
                  "Scanning ports 9371 - 9390",
                  style: TextStyle(fontSize: 12, color: Colors.grey),
                ),
                const SizedBox(height: 48),
                OutlinedButton(
                  onPressed: _handleCancel,
                  child: const Text("Stop & Cancel"),
                ),
              ] 
              
              // --- STATE: SERVER FOUND ---
              else if (_serverInfo != null) ...[
                const Icon(Icons.lan_rounded, size: 80, color: Colors.green),
                const SizedBox(height: 24),
                const Text(
                  "Server Found!",
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                Card(
                  elevation: 0,
                  color: Colors.green.shade50,
                  shape: RoundedRectangleBorder(
                    side: BorderSide(color: Colors.green.shade200),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: ListTile(
                    leading: const Icon(Icons.computer, color: Colors.green),
                    title: Text("IP: ${_serverInfo!['serverIp']}"),
                    subtitle: Text("Port: ${_serverInfo!['serverPort']}"),
                  ),
                ),
                const SizedBox(height: 32),
                ElevatedButton(
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) =>
                            ClientConfigurationScreen(serverInfo: _serverInfo!),
                      ),
                    );
                  },
                  style: ElevatedButton.styleFrom(
                    minimumSize: const Size(200, 50),
                    backgroundColor: Colors.blueGrey.shade800,
                    foregroundColor: Colors.white,
                  ),
                  child: const Text("Connect to Server"),
                ),
                TextButton(
                  onPressed: _handleCancel,
                  child: const Text("Cancel"),
                ),
              ] 
              
              // --- STATE: ERROR / NOT FOUND ---
              else ...[
                const Icon(Icons.wifi_off_rounded, size: 80, color: Colors.red),
                const SizedBox(height: 24),
                Text(
                  _errorMessage!,
                  textAlign: TextAlign.center,
                  style: const TextStyle(color: Colors.black87),
                ),
                const SizedBox(height: 32),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    OutlinedButton(
                      onPressed: _handleCancel,
                      style: OutlinedButton.styleFrom(
                        foregroundColor: Colors.blueGrey.shade800,
                      ),
                      child: const Text("Cancel"),
                    ),
                    const SizedBox(width: 16),
                    ElevatedButton(
                      onPressed: _startDiscovery,
                      child: const Text("Retry Scan"),
                    ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }
}