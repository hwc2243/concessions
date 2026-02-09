import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/app_config_provider.dart';

class SystemSetupScreen extends StatelessWidget {
  const SystemSetupScreen({super.key});

  @override
  Widget build(BuildContext context) {
    // Watch the internet status from the provider to reactively update the UI
    final hasInternet = context.watch<AppConfigProvider>().hasInternet;

    return Scaffold(
      backgroundColor: Colors.white,
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const Icon(Icons.store_rounded, size: 80, color: Colors.blueGrey),
              const SizedBox(height: 24),
              const Text(
                'Welcome to Concessions POS',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 28, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 8),
              const Text(
                'Choose how you would like to begin.',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 16, color: Colors.grey),
              ),
              const SizedBox(height: 48),

              // Service Setup: Only available if Internet is present
              if (hasInternet)
                context.watch<AppConfigProvider>().isAuthenticating
                    ? const Center(child: CircularProgressIndicator())
                    : _SetupCard(
                        title: 'Setup a new system',
                        subtitle:
                            'This device will act as the location server.',
                        icon: Icons.dns_rounded,
                        color: Colors.blue.shade700,
                        onTap: () =>
                            context.read<AppConfigProvider>().loginAndSetServer(),
                      )
              else
                _buildNoInternetWarning(context),

              const SizedBox(height: 16),

              // Client Setup: Always available (assumes local location network)
              _SetupCard(
                title: 'Add to existing system',
                subtitle: 'Connect this device to an active location network.',
                icon: Icons.add_link_rounded,
                color: Colors.green.shade700,
                onTap: () {
                  context.read<AppConfigProvider>().setRole(POSRole.client);
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildNoInternetWarning(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: Colors.amber.shade50,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: Colors.amber.shade200),
      ),
      child: Column(
        children: [
          Row(
            children: [
              Icon(Icons.wifi_off_rounded, color: Colors.amber.shade900),
              const SizedBox(width: 16),
              const Expanded(
                child: Text(
                  'To setup a new system, please ensure an active internet connection.',
                  style: TextStyle(
                    fontWeight: FontWeight.w500,
                    color: Colors.black87,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Align(
            alignment: Alignment.centerRight,
            child: TextButton.icon(
              onPressed: () =>
                  context.read<AppConfigProvider>().checkConnectivity(),
              icon: const Icon(Icons.refresh_rounded, size: 20),
              label: const Text('Refresh Status'),
              style: TextButton.styleFrom(
                foregroundColor: Colors.amber.shade900,
                visualDensity: VisualDensity.compact,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _SetupCard extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  const _SetupCard({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(16),
      child: Container(
        padding: const EdgeInsets.all(20),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey.shade300),
          borderRadius: BorderRadius.circular(16),
        ),
        child: Row(
          children: [
            CircleAvatar(
              backgroundColor: color.withOpacity(0.1),
              child: Icon(icon, color: color),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  Text(
                    subtitle,
                    style: TextStyle(fontSize: 14, color: Colors.grey.shade600),
                  ),
                ],
              ),
            ),
            const Icon(Icons.chevron_right, color: Colors.grey),
          ],
        ),
      ),
    );
  }
}
