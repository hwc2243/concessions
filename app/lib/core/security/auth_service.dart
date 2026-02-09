import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_appauth/flutter_appauth.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:jwt_decoder/jwt_decoder.dart';

class AuthService {
  final FlutterAppAuth _appAuth = const FlutterAppAuth();
  final FlutterSecureStorage _secureStorage = const FlutterSecureStorage();

  final String _clientId = 'app';
  final String _redirectUri = 'com.concessions://callback';
  final String _discoveryUri = 
      'https://login.connors.ddns.net/realms/concession/.well-known/openid-configuration';

  /// Starts the OIDC Authorization Code Flow
  Future<bool> login() async {
    try {
      final AuthorizationTokenResponse? result = await _appAuth.authorizeAndExchangeCode(
        AuthorizationTokenRequest(
          _clientId,
          _redirectUri,
          discoveryUrl: _discoveryUri,
          scopes: ['openid', 'profile', 'email'],
          //preferEphemeralSession: true,
        ),
      );

      if (result != null) {
        await _secureStorage.write(key: 'access_token', value: result.accessToken);
        await _secureStorage.write(key: 'id_token', value: result.idToken);
        await _secureStorage.write(key: 'refresh_token', value: result.refreshToken);
        return true;
      }
    } catch (e) {
      print('Login error: $e');
    }
    return false;
  }

  /// Logs the user out by ending the session on the server
  Future<void> logout() async {
    try {
      final String? idToken = await _secureStorage.read(key: 'id_token');

      await _appAuth.endSession(
        EndSessionRequest(
          idTokenHint: idToken,
          postLogoutRedirectUrl: _redirectUri,
          discoveryUrl: _discoveryUri,
        ),
      );

      print('Logged out successfully');
    } catch (e) {
      print('Logout error: $e');
    } finally {
      await _secureStorage.deleteAll();
    }
  }

Future<bool> refreshToken() async {
  try {
    final String? storedRefreshToken = await _secureStorage.read(key: 'refresh_token');

    if (storedRefreshToken == null) return false;

    final TokenResponse? result = await _appAuth.token(
      TokenRequest(
        _clientId,
        _redirectUri,
        discoveryUrl: _discoveryUri,
        refreshToken: storedRefreshToken,
        scopes: ['openid', 'profile', 'email', 'offline_access'],
      ),
    );

    if (result != null) {
      await _secureStorage.write(key: 'access_token', value: result.accessToken);
      await _secureStorage.write(key: 'id_token', value: result.idToken);
      await _secureStorage.write(key: 'refresh_token', value: result.refreshToken);
      return true;
    }
  } catch (e) {
    print('Refresh token error: $e');
    // If the refresh token itself is expired, we should log the user out
    await logout();
  }
  return false;
}

Future<bool> hasToken() async {
  String? token = await _secureStorage.read(key: 'access_token');

  if (token != null) {
    if (JwtDecoder.isExpired(token)) {
      print("Token expired, refreshing...");
      await refreshToken(); 
      token = await _secureStorage.read(key: 'access_token');
    }

    if (token != null) {
      final pattern = RegExp('.{1,500}');
      print("--- START ACCESS TOKEN ---");
      pattern.allMatches(token).forEach((match) => print(match.group(0)));
      print("--- END ACCESS TOKEN ---");
      
      await Clipboard.setData(ClipboardData(text: token));
    }
  }

  return token != null;
}

Future<String?> getAccessToken() async {
  String? token = await _secureStorage.read(key: 'access_token');

  if (token != null) {
    bool isExpired = JwtDecoder.isExpired(token);
    
    if (isExpired) {
      print("Token expired. Attempting refresh...");
      bool refreshed = await refreshToken();
      if (refreshed) {
        return await _secureStorage.read(key: 'access_token');
      } else {
        return null; 
      }
    }
  }
  return token;
}
}