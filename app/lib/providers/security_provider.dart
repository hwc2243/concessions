import 'package:flutter/material.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecurityProvider extends ChangeNotifier {
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  
  String? _systemPin;
  String? get systemPin => _systemPin;

  // Key used in secure storage
  static const String _pinKey = 'system_security_pin';

  /// Call this when the app starts
  Future<void> initializePin() async {
    _systemPin = await _storage.read(key: _pinKey);
    notifyListeners();
  }

  /// Call this during Local Server Setup
  Future<void> savePin(String newPin) async {
    await _storage.write(key: _pinKey, value: newPin);
    _systemPin = newPin;
    notifyListeners();
  }

  /// Security check helper
  bool verifyPin(String input) {
    return _systemPin == input;
  }
}