import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import '/models/device_type.dart';
import '/models/dto/pin_verify_request.dart';
import '/models/dto/simple_response.dart';
import '/network/network_constants.dart';
import '/network/messenger_service.dart';
import '../providers/app_config_provider.dart';
import '/providers/security_provider.dart';
import '/providers/server_config_provider.dart';

class ClientConfigurationScreen extends StatefulWidget {
  final Map<String, dynamic> serverInfo;

  const ClientConfigurationScreen({super.key, required this.serverInfo});

  @override
  State<ClientConfigurationScreen> createState() =>
      _ClientConfigurationScreenState();
}

class _ClientConfigurationScreenState extends State<ClientConfigurationScreen> {
  final _formKey = GlobalKey<FormState>();
  final _pinController = TextEditingController();
  final _confirmPinController = TextEditingController();
  DeviceTypeType _selectedType = DeviceTypeType.POS;
  bool _isLoading = false;

  void _completeClientSetup() async {
    if (_formKey.currentState!.validate()) {
      setState(() => _isLoading = true);

      final security = context.read<SecurityProvider>();
      final appConfig = context.read<AppConfigProvider>();
      final serverConfig = context.read<ServerConfigProvider>();

      final targetIp = serverConfig.serverIp ?? widget.serverInfo['serverIp'];
      final targetPort =
          serverConfig.serverPort ?? widget.serverInfo['serverPort'];

      if (targetIp == null || targetPort == null) {
        setState(() => _isLoading = false);
        _showErrorSnackBar("Connection details missing. Please restart setup.");
        return;
      }

      try {
        await MessengerService.sendRequest<SimpleResponseDTO>(
          serverIp: targetIp,
          serverPort: targetPort,
          service: NetworkConstants.pinService,
          action: NetworkConstants.pinVerifyAction,
          payload: PINVerifyRequestDTO(pin: _pinController.text).toJson(),
          fromJson: (json) => SimpleResponseDTO.fromJson(json),
        );

        if (serverConfig.serverIp == null) {
          await serverConfig.saveManualConfig(
            serverIp: targetIp,
            serverPort: targetPort,
          );
        }

        await security.savePin(_pinController.text);
        await appConfig.saveClientConfiguration(POSRole.client, _selectedType);

        if (mounted) {
          Navigator.of(context).popUntil((route) => route.isFirst);
        }
      } catch (e) {
        if (mounted) {
          _pinController.clear();
          _confirmPinController.clear();
          setState(() => _isLoading = false);
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(
                e.toString().replaceAll("MessengerException: ", ""),
              ),
              backgroundColor: Colors.redAccent,
            ),
          );
        }
      }
    }
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

  Widget build(BuildContext context) {
    // 2. Wrap everything in a Stack to place the overlay on top
    return Scaffold(
      appBar: AppBar(title: const Text("Device Configuration")),
      body: Stack(
        children: [
          SingleChildScrollView(
            padding: const EdgeInsets.all(24.0),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const Text(
                    "Client Configuration",
                    style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    "Connected to Server at ${widget.serverInfo['serverIp']}",
                  ),
                  const SizedBox(height: 32),

                  // PIN Fields (PIN and Confirm PIN)
                  TextFormField(
                    controller: _pinController,
                    enabled: !_isLoading, // Disable during loading
                    obscureText: true,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: "System PIN",
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.lock_outline),
                    ),
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly,
                      LengthLimitingTextInputFormatter(6),
                    ],
                    validator: (val) => (val == null || val.length < 6)
                        ? "Enter 6-digit PIN"
                        : null,
                  ),
                  const SizedBox(height: 20),
                  TextFormField(
                    controller: _confirmPinController,
                    enabled: !_isLoading, // Disable during loading
                    obscureText: true,
                    keyboardType: TextInputType.number,
                    decoration: const InputDecoration(
                      labelText: "Confirm PIN",
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.lock_reset),
                    ),
                    inputFormatters: [
                      FilteringTextInputFormatter.digitsOnly,
                      LengthLimitingTextInputFormatter(6),
                    ],
                    validator: (val) => (val != _pinController.text)
                        ? "PINs do not match"
                        : null,
                  ),
                  const SizedBox(height: 20),

                  // Device Type Dropdown
                  DropdownButtonFormField<DeviceTypeType>(
                    value: _selectedType,
                    decoration: const InputDecoration(
                      labelText: "Device Type",
                      border: OutlineInputBorder(),
                      prefixIcon: Icon(Icons.devices),
                    ),
                    items: DeviceTypeType.values
                        .where((type) => type != DeviceTypeType.SERVER)
                        .map(
                          (type) => DropdownMenuItem(
                            value: type,
                            child: Text(type.value),
                          ),
                        )
                        .toList(),
                    onChanged: _isLoading
                        ? null
                        : (val) => setState(() => _selectedType = val!),
                  ),
                  const SizedBox(height: 40),

                  ElevatedButton(
                    onPressed: _isLoading ? null : _completeClientSetup,
                    style: ElevatedButton.styleFrom(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                    ),
                    child: _isLoading
                        ? const SizedBox(
                            height: 20,
                            width: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Text("Complete Configuration"),
                  ),
                ],
              ),
            ),
          ),

          // 3. The Loading Overlay
          if (_isLoading)
            Container(
              color: Colors.white.withOpacity(0.7),
              child: const Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    CircularProgressIndicator(),
                    SizedBox(height: 16),
                    Text(
                      "Securing Device...",
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}
