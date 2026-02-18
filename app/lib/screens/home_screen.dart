import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:network_info_plus/network_info_plus.dart';
import 'package:provider/provider.dart';
import '/models/device_type.dart';
import '/providers/app_config_provider.dart';
import '/providers/journal_provider.dart';
import '/providers/kitchen_provider.dart';
import '/providers/client_config_provider.dart';
import '/providers/menu_provider.dart';
import '/providers/security_provider.dart';
import '/providers/server_config_provider.dart';
import 'kitchen_screen.dart';
import 'order_entry_screen.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String _status = "Starting...";
  int _selectedIndex = 0; // 0 for Home, 1 for Settings
  bool _showManualEntry = false;
  final _manualIpController = TextEditingController();
  final _manualPortController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _initApp();
  }

  Future<void> _initApp() async {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      final appConfig = context.read<AppConfigProvider>();
      final serverConfig = context.read<ServerConfigProvider>();

      // Only search if we are a client
      if (appConfig.role == POSRole.client) {
        if (serverConfig.serverIp == null || serverConfig.serverIp!.isEmpty) {
          setState(() {
            _status = "Searching for server...";
            _showManualEntry = false;
          });

          bool found = await serverConfig.discoverServer();

          if (!found && mounted) {
            setState(() {
              _status = "Server Not Found";
              _showManualEntry = true; // Show manual UI option
            });
            return;
          }
        }

        // If we have an IP (either from provider or just discovered), proceed
        _establishConnection();
      }
    });
  }

  Future<void> _establishConnection() async {
    final appConfig = context.read<AppConfigProvider>();
    final clientConfig = context.read<ClientConfigProvider>();
    final serverConfig = context.read<ServerConfigProvider>();
    final security = context.read<SecurityProvider>();
    final journalProvider = context.read<JournalProvider>();
    final kitchenProvider = context.read<KitchenProvider>();
    final menuProvider = context.read<MenuProvider>();

    try {
      setState(() {
        _status = "Starting client networking...";
        _showManualEntry = false;
      });

      await clientConfig.startListener(journalProvider, kitchenProvider);

      setState(() => _status = "Registering...");
      await serverConfig.registerWithServer(
        pin: security.systemPin ?? "",
        deviceType: appConfig.deviceType?.value ?? "POS",
        clientConfig: clientConfig,
      );

      setState(() => _status = "Retrieving configuration...");
      await appConfig.loadLocationConfiguration(
        clientConfig: clientConfig,
        serverConfig: serverConfig,
        security: security,
      );

      if (appConfig.deviceType == DeviceTypeType.POS) {
        setState(() => _status = "Fetching Journal...");
        await journalProvider.loadJournal(
          serverConfig: serverConfig,
          clientConfig: clientConfig,
          security: security,
        );

        setState(() => _status = "Fetching Menu...");
        await menuProvider.loadMenu(
          serverConfig: serverConfig,
          clientConfig: clientConfig,
          security: security,
        );
      } else if (appConfig.deviceType == DeviceTypeType.KITCHEN) {
        setState(() => _status = "Fetching orders...");
        await kitchenProvider.loadInitialOrders(
          serverConfig: serverConfig,
          clientConfig: clientConfig,
          security: security,
        );
      }

      setState(() => _status = "Ready");
    } catch (e) {
      setState(() => _status = "Setup Failed: $e");
    }
  }

  void _handleFullReset() {
    context.read<AppConfigProvider>().resetConfiguration();
    context.read<ClientConfigProvider>().stopListener();
    context.read<ClientConfigProvider>().clear();
    context.read<ServerConfigProvider>().disconnect();

    // Use popUntil to clear the stack or simply trust the Gatekeeper/Main switch
    // to rebuild and show the SystemSetupScreen.
    Navigator.of(context).popUntil((route) => route.isFirst);
  }

  void _retryConnection() {
    setState(() => _status = "Retrying...");
    _initApp();
  }

  void _onItemTapped(int index) {
    if (index == 1) {
      _showSettingsDialog();
    } else {
      setState(() => _selectedIndex = index);
    }
  }

  void _showSettingsDialog() async {
    final info = NetworkInfo();
    String? wifiIP = await info.getWifiIP(); // Get this device's local IP
    final clientConfig = context.read<ClientConfigProvider>();
    final serverConfig = context.read<ServerConfigProvider>();
    final appConfig = context.read<AppConfigProvider>();

    if (!mounted) return;

    showModalBottomSheet(
      context: context,
      isScrollControlled:
          true, // Allow it to take up more screen height if needed
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      builder: (context) {
        return DraggableScrollableSheet(
          initialChildSize: 0.6, // Start at 60% of screen height
          maxChildSize: 0.9, // Expand up to 90%
          minChildSize: 0.4,
          expand: false,
          builder: (context, scrollController) {
            return SafeArea(
              child: ListView(
                // Using ListView makes the content scrollable
                controller: scrollController,
                padding: const EdgeInsets.symmetric(vertical: 8),
                children: [
                  const ListTile(
                    title: Text(
                      "System Settings",
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    leading: Icon(Icons.settings_suggest),
                  ),
                  const Divider(),

                  // Connection Info Section
                  Padding(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 8,
                    ),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          "Location Details",
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.blueGrey,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 4),
                        _buildInfoRow(
                          "Organization:",
                          appConfig.organizationName ?? "N/A",
                        ),
                        _buildInfoRow(
                          "Location:",
                          appConfig.locationName ?? "N/A",
                        ),
                        _buildInfoRow("Menu:", appConfig.menuName ?? "N/A"),

                        const SizedBox(height: 16), // Spacer between sections

                        const Text(
                          "Network Details",
                          style: TextStyle(
                            fontSize: 12,
                            color: Colors.blueGrey,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 8),
                        _buildInfoRow(
                          "Terminal Number:",
                          clientConfig.deviceNumber ?? "N/A",
                        ),
                        _buildInfoRow(
                          "Client Role:",
                          appConfig.deviceType?.value ?? "N/A",
                        ),
                        _buildInfoRow(
                          "Client Id:",
                          clientConfig.deviceId ?? "N/A",
                        ),
                        _buildInfoRow(
                          "Client Address:",
                          "${clientConfig.deviceIp ?? "Unknown"}:${clientConfig.devicePort}",
                        ),
                        _buildInfoRow(
                          "Server Address:",
                          "${serverConfig.serverIp ?? "Not Connected"}:${serverConfig.serverPort}",
                        ),
                      ],
                    ),
                  ),

                  const Divider(),
                  ListTile(
                    leading: const Icon(Icons.refresh, color: Colors.red),
                    title: const Text(
                      "Reset Configuration",
                      style: TextStyle(color: Colors.red),
                    ),
                    onTap: () {
                      Navigator.pop(context);
                      _confirmReset();
                    },
                  ),
                ],
              ),
            );
          },
        );
      },
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: Colors.deepPurple.withOpacity(0.1)),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.02),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: ListTile(
        // The whole row is now the touch target
        onTap: () => _copyToClipboard(label, value),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
        title: Text(
          label,
          style: TextStyle(
            fontSize: 14, // Increased from 12
            fontWeight: FontWeight.w600,
            color: Colors.blueGrey.shade800,
          ),
        ),
        trailing: Container(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
          decoration: BoxDecoration(
            color: Colors.deepPurple.withOpacity(0.05),
            borderRadius: BorderRadius.circular(6),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                value,
                style: const TextStyle(
                  fontFamily: 'Courier',
                  fontWeight: FontWeight.bold,
                  fontSize: 16, // Increased from 14
                  color: Colors.deepPurple,
                ),
              ),
              const SizedBox(width: 10),
              Icon(
                Icons.copy_rounded,
                size: 18,
                color: Colors.deepPurple.withOpacity(0.5),
              ),
            ],
          ),
        ),
      ),
    );
  }

  // Separate helper method to keep logic clean
  void _copyToClipboard(String label, String value) {
    Clipboard.setData(ClipboardData(text: value)).then((_) {
      if (mounted) {
        ScaffoldMessenger.of(context).hideCurrentSnackBar(); // Clear existing
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("Copied $label: $value"),
            behavior: SnackBarBehavior.floating,
            backgroundColor: Colors.deepPurple,
            duration: const Duration(seconds: 1),
          ),
        );
      }
    });
  }

  /*
  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 2),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: const TextStyle(fontWeight: FontWeight.w500)),
          Text(
            value,
            style: const TextStyle(fontFamily: 'Courier'),
          ), // Monospaced for IPs
        ],
      ),
    );
  }
  */

  void _confirmReset() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text("Are you sure?"),
        content: const Text(
          "All local settings will be deleted. This cannot be undone.",
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text("CANCEL"),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              context.read<AppConfigProvider>().resetConfiguration();
              context.read<ClientConfigProvider>().stopListener();
              context.read<ClientConfigProvider>().clear();
              context.read<ServerConfigProvider>().disconnect();
              context.read<ServerConfigProvider>().discoverServer();
            },
            child: const Text("RESET", style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final appConfig = context.watch<AppConfigProvider>();
    final clientConfig = context.watch<ClientConfigProvider>();

    // Create a dynamic title
    String title = "Dashboard";
    if (appConfig.role == POSRole.client && clientConfig.deviceNumber != null) {
      title = "Terminal #${clientConfig.deviceNumber}";
    }

    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        actions: [
          // Quick indicator for server connection
          Padding(
            padding: const EdgeInsets.only(right: 16.0),
            child: Icon(
              Icons.circle,
              size: 12,
              color: context.watch<ServerConfigProvider>().isConnected
                  ? Colors.green
                  : Colors.red,
            ),
          ),
        ],
      ),
      body: _buildBody(),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _selectedIndex,
        onTap: _onItemTapped,
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.home), label: 'Home'),
          BottomNavigationBarItem(
            icon: Icon(Icons.settings),
            label: 'Settings',
          ),
        ],
      ),
    );
  }

  Widget _buildBody() {
    // If user explicitly chose manual mode
    if (_showManualEntry) {
      return _buildManualConfigUI();
    }

    final appConfig = context.watch<AppConfigProvider>();
    final serverConfig = context.watch<ServerConfigProvider>();
    final menuProvider = context.watch<MenuProvider>();
    final kitchenProvider = context.watch<KitchenProvider>();

    // --- STATE: CONNECTION FAILED / NOT FOUND ---
    // This is the specific UI you requested for failures
    if (_status.contains("Failed") || _status.contains("Not Found")) {
      return Center(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Icon(Icons.wifi_off_rounded, size: 80, color: Colors.red),
              const SizedBox(height: 24),
              Text(
                _status,
                textAlign: TextAlign.center,
                style: const TextStyle(fontSize: 18, color: Colors.black87),
              ),
              const SizedBox(height: 32),

              // Option 1: Retry Scan
              ElevatedButton.icon(
                onPressed: _retryConnection,
                icon: const Icon(Icons.refresh),
                label: const Text("Retry Auto-Scan"),
                style: ElevatedButton.styleFrom(
                  minimumSize: const Size(250, 50),
                ),
              ),
              const SizedBox(height: 12),

              // Option 2: Manual IP
              OutlinedButton.icon(
                onPressed: () => setState(() => _showManualEntry = true),
                icon: const Icon(Icons.edit),
                label: const Text("Enter IP Manually"),
                style: OutlinedButton.styleFrom(
                  minimumSize: const Size(250, 50),
                ),
              ),
              const SizedBox(height: 24),

              // Option 3: Cancel (Full Reset)
              TextButton(
                onPressed: _handleFullReset,
                child: const Text(
                  "Cancel & Reset Configuration",
                  style: TextStyle(color: Colors.red),
                ),
              ),
            ],
          ),
        ),
      );
    }

    // --- STATE: LOADING / INITIALIZING ---
    bool isServerConnected = serverConfig.isConnected;
    bool isDataLoaded = _status == "Ready";

    if (appConfig.role == POSRole.client &&
        (!isServerConnected || !isDataLoaded)) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const CircularProgressIndicator(),
            const SizedBox(height: 16),
            Text(_status, style: const TextStyle(fontWeight: FontWeight.bold)),
          ],
        ),
      );
    }

    // --- STATE: READY ---
    if (appConfig.role == POSRole.client) {
      if (appConfig.deviceType == DeviceTypeType.POS) {
        return const OrderEntryScreen();
      } else if (appConfig.deviceType == DeviceTypeType.KITCHEN) {
        return const KitchenScreen();
      }
    }

    return const Center(child: Text("Welcome"));
  }

  Widget _buildManualConfigUI() {
    return Center(
      child: SingleChildScrollView(
        padding: const EdgeInsets.all(32),
        child: Column(
          children: [
            const Icon(
              Icons.settings_ethernet,
              size: 64,
              color: Colors.blueGrey,
            ),
            const SizedBox(height: 16),
            const Text(
              "Manual Server Setup",
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 24),
            TextField(
              controller: _manualIpController,
              autocorrect: false,
              decoration: const InputDecoration(
                labelText: "Server IP",
                border: OutlineInputBorder(),
                hintText: "e.g. 192.168.1.50",
              ),
              keyboardType: const TextInputType.numberWithOptions(
                decimal: true,
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _manualPortController,
              decoration: const InputDecoration(
                labelText: "Port",
                border: OutlineInputBorder(),
              ),
              keyboardType: TextInputType.number,
            ),
            const SizedBox(height: 32),
            ElevatedButton(
              onPressed: () async {
                final ip = _manualIpController.text.trim();
                final portStr = _manualPortController.text.trim();

                if (ip.isEmpty) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text("Please enter an IP address")),
                  );
                  return;
                }

                await context.read<ServerConfigProvider>().saveManualConfig(
                  serverIp: ip,
                  serverPort: int.tryParse(portStr) ?? 8080,
                );

                setState(() => _showManualEntry = false);
                _establishConnection();
              },
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 50),
              ),
              child: const Text("Save & Connect"),
            ),
            TextButton(
              onPressed: () => setState(() => _showManualEntry = false),
              child: const Text("Back to Selection"),
            ),
          ],
        ),
      ),
    );
  }
}
