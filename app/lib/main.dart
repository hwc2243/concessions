import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/app_config_provider.dart';
import 'providers/client_config_provider.dart';
import 'providers/journal_provider.dart';
import 'providers/kitchen_provider.dart';
import 'providers/menu_provider.dart';
import 'providers/order_provider.dart';
import 'providers/security_provider.dart';
import 'providers/server_config_provider.dart';
import 'screens/client_setup_screen.dart';
import 'screens/home_screen.dart';
import 'screens/local_server_setup_screen.dart';
import 'screens/system_setup_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final securityProvider = SecurityProvider();
  await securityProvider.initializePin();

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: securityProvider),
        ChangeNotifierProvider(create: (_) => AppConfigProvider()),
        ChangeNotifierProvider(create: (_) => ClientConfigProvider()),
        ChangeNotifierProvider(create: (_) => ServerConfigProvider()),
        ChangeNotifierProvider(create: (_) => JournalProvider()),
        ChangeNotifierProvider(create: (_) => KitchenProvider()),
        ChangeNotifierProvider(create: (_) => MenuProvider()),
        ChangeNotifierProvider(create: (_) => OrderProvider()),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData(colorSchemeSeed: Colors.blueGrey, useMaterial3: true),
      home: Consumer<AppConfigProvider>(
        builder: (context, appConfig, _) {

          if (appConfig.isConfigured) {
            return const HomeScreen();
          }
          else if (appConfig.role == POSRole.undecided) {
            return const SystemSetupScreen();
          }
          else if (appConfig.role == POSRole.server) {
            return const LocalServerSetupScreen();
          }
          else if (appConfig.role == POSRole.client) {
            return const ClientSetupScreen();
          }

          // Fallback
          return const Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );
        },
      ),
    );
  }
}
