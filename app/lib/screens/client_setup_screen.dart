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
  bool _isManualMode = false;
  Map<String, dynamic>? _serverInfo;
  String? _errorMessage;

  final TextEditingController _ipController = TextEditingController();
  final TextEditingController _portController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _startDiscovery();
  }

  @override
  void dispose() {
    _ipController.dispose();
    _portController.dispose();
    super.dispose();
  }

  Future<void> _startDiscovery() async {
    setState(() {
      _isScanning = true;
      _isManualMode = false;
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

  void _handleManualConnect() {
    if (!_isValidInput()) return;

    final ip = _ipController.text.trim();
    final port = int.tryParse(_portController.text.trim());

    if (ip.isEmpty || port == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text("Please enter a valid IP and Port")),
      );
      return;
    }

    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ClientConfigurationScreen(
          serverInfo: {'serverIp': ip, 'serverPort': port},
        ),
      ),
    );
  }

  void _handleCancel() {
    context.read<AppConfigProvider>().setRole(POSRole.undecided);
  }

  bool _isValidInput() {
    final ip = _ipController.text.trim();
    final portStr = _portController.text.trim();

    // IPv4 Regex: Matches 4 groups of 1-3 digits separated by dots
    final ipRegex = RegExp(
      r'^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$',
    );

    if (!ipRegex.hasMatch(ip)) {
      _showErrorSnackBar(
        "Please enter a valid IPv4 address (e.g., 192.168.1.50)",
      );
      return false;
    }

    final port = int.tryParse(portStr);
    if (port == null || port < 1 || port > 65535) {
      _showErrorSnackBar("Port must be a number between 1 and 65535");
      return false;
    }

    return true;
  }

  void _showErrorSnackBar(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: Colors.red.shade800,
        behavior: SnackBarBehavior.floating,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Client Setup"),
        automaticallyImplyLeading: false,
      ),
      body: SingleChildScrollView(
        // Added to prevent overflow when keyboard appears
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const SizedBox(height: 40),
                if (_isManualMode)
                  _buildManualInput()
                else if (_isScanning)
                  _buildScanningView()
                else if (_serverInfo != null)
                  _buildFoundView()
                else
                  _buildErrorView(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildScanningView() {
    return Column(
      children: [
        const CircularProgressIndicator(),
        const SizedBox(height: 24),
        const Text(
          "Searching for Server...",
          style: TextStyle(fontSize: 18, fontWeight: FontWeight.w500),
        ),
        const Text(
          "Scanning ports 9371 - 9390",
          style: TextStyle(fontSize: 12, color: Colors.grey),
        ),
        const SizedBox(height: 48),
        OutlinedButton(
          onPressed: _handleCancel,
          child: const Text("Stop & Cancel"),
        ),
      ],
    );
  }

  Widget _buildFoundView() {
    return Column(
      children: [
        const Icon(Icons.lan_rounded, size: 80, color: Colors.green),
        const SizedBox(height: 24),
        const Text(
          "Server Found!",
          style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 16),
        _serverCard(
          _serverInfo!['serverIp'],
          _serverInfo!['serverPort'].toString(),
        ),
        const SizedBox(height: 32),
        ElevatedButton(
          onPressed: () => Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) =>
                  ClientConfigurationScreen(serverInfo: _serverInfo!),
            ),
          ),
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(200, 50),
            backgroundColor: Colors.blueGrey.shade800,
            foregroundColor: Colors.white,
          ),
          child: const Text("Connect to Server"),
        ),
        TextButton(onPressed: _startDiscovery, child: const Text("Rescan")),
      ],
    );
  }

  Widget _buildErrorView() {
    return Column(
      children: [
        const Icon(Icons.wifi_off_rounded, size: 80, color: Colors.red),
        const SizedBox(height: 24),
        Text(_errorMessage!, textAlign: TextAlign.center),
        const SizedBox(height: 32),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            OutlinedButton(
              onPressed: _handleCancel,
              child: const Text("Cancel"),
            ),
            const SizedBox(width: 16),
            ElevatedButton(
              onPressed: _startDiscovery,
              child: const Text("Retry Scan"),
            ),
          ],
        ),
        const SizedBox(height: 16),
        TextButton(
          onPressed: () => setState(() => _isManualMode = true),
          child: const Text("Enter IP Manually"),
        ),
      ],
    );
  }

  Widget _buildManualInput() {
    return Column(
      children: [
        const Icon(Icons.settings_ethernet, size: 60, color: Colors.blueGrey),
        const SizedBox(height: 16),
        const Text(
          "Manual Configuration",
          style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 24),
        TextField(
          controller: _ipController,
          autocorrect: false,
          enableSuggestions: false,
          decoration: const InputDecoration(
            labelText: "Server IP Address",
            border: OutlineInputBorder(),
            hintText: "e.g. 192.168.1.50",
          ),
          keyboardType: const TextInputType.numberWithOptions(decimal: true),
        ),
        const SizedBox(height: 16),
        TextField(
          controller: _portController,
          decoration: const InputDecoration(
            labelText: "Port",
            border: OutlineInputBorder(),
          ),
          keyboardType: TextInputType.number,
        ),
        const SizedBox(height: 32),
        ElevatedButton(
          onPressed: _handleManualConnect,
          style: ElevatedButton.styleFrom(
            minimumSize: const Size(double.infinity, 50),
            backgroundColor: Colors.green.shade700,
            foregroundColor: Colors.white,
          ),
          child: const Text("Connect Manually"),
        ),
        TextButton(
          onPressed: () => setState(() => _isManualMode = false),
          child: const Text("Back to Scan"),
        ),
      ],
    );
  }

  Widget _serverCard(String ip, String port) {
    return Card(
      elevation: 0,
      color: Colors.green.shade50,
      shape: RoundedRectangleBorder(
        side: BorderSide(color: Colors.green.shade200),
        borderRadius: BorderRadius.circular(12),
      ),
      child: ListTile(
        leading: const Icon(Icons.computer, color: Colors.green),
        title: Text("IP: $ip"),
        subtitle: Text("Port: $port"),
      ),
    );
  }
}
