import 'dart:io';
import 'package:device_info_plus/device_info_plus.dart';

class DeviceUtil {
  static Future<String> getUniqueId() async {
    final deviceInfo = DeviceInfoPlugin();
    
    if (Platform.isAndroid) {
      final androidInfo = await deviceInfo.androidInfo;
      return androidInfo.id; // Usually the hardware serial or a unique build ID
    } else if (Platform.isIOS) {
      final iosInfo = await deviceInfo.iosInfo;
      return iosInfo.identifierForVendor ?? "unknown_ios_device";
    }
    
    return "unknown_platform_device";
  }
}