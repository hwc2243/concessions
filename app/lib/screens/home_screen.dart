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

  @override
  void initState() {
    super.initState();
    _initApp();
  }

  Future<void> _initApp() async {
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      final appConfig = context.read<AppConfigProvider>();
      final clientConfig = context.read<ClientConfigProvider>();
      final serverConfig = context.read<ServerConfigProvider>();
      final security = context.read<SecurityProvider>();
      final journalProvider = context.read<JournalProvider>();
      final kitchenProvider = context.read<KitchenProvider>();
      final menuProvider = context.read<MenuProvider>();

      // Only search if we are a client
      if (appConfig.role == POSRole.client) {
        setState(() => _status = "Searching for server...");
        bool found = await serverConfig.discoverServer();
        if (!found) {
          setState(() => _status = "Server Not Found");
          return;
        }
        if (found && mounted) {
          try {
            setState(() => _status = "Starting client networking...");
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
        } else {
          setState(() => _status = "Server Not Found");
        }
      }
    });
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
    final appConfig = context.watch<AppConfigProvider>();
    final serverConfig = context.watch<ServerConfigProvider>();
    final menuProvider = context.watch<MenuProvider>();
    final kitchenProvider = context.watch<KitchenProvider>();

    // 1. Handle Errors
    if (_status.contains("Failed") || _status.contains("Not Found")) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.error_outline, color: Colors.red, size: 48),
            const SizedBox(height: 16),
            Text(_status, textAlign: TextAlign.center),
            const SizedBox(height: 24),
            ElevatedButton(
              onPressed: _retryConnection,
              child: const Text("Retry Connection"),
            ),
          ],
        ),
      );
    }

    // 2. Loading State (Conditional based on Device Type)
    bool isServerConnected = serverConfig.isConnected;
    bool isDataLoaded = false;

    if (appConfig.deviceType == DeviceTypeType.POS) {
      isDataLoaded = menuProvider.menu != null;
    } else if (appConfig.deviceType == DeviceTypeType.KITCHEN) {
      // For kitchen, we consider it "loaded" even if the list is empty,
      // as long as the initial fetch completed (status is "Ready")
      isDataLoaded = _status == "Ready";
    }

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

    // 3. Normal Screens
    if (appConfig.role == POSRole.client) {
      if (appConfig.deviceType == DeviceTypeType.POS) {
        return const OrderEntryScreen();
      } else if (appConfig.deviceType == DeviceTypeType.KITCHEN) {
        return const KitchenScreen();
      }
    }

    return const Center(child: Text("Welcome"));
  }
}
