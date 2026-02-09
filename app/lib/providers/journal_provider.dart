import 'package:flutter/material.dart';
import '/models/dto/journal_dto.dart';
import '/network/messenger_service.dart';
import '/network/network_constants.dart';
import 'client_config_provider.dart';
import 'server_config_provider.dart';
import 'security_provider.dart';

class JournalProvider extends ChangeNotifier {
  JournalDTO? _journal;
  JournalDTO? get journal => _journal;

  bool _isLoading = false;
  bool get isLoading => _isLoading;

  bool get isOpen => _journal?.status == StatusType.OPEN;
  bool get isLocked => _journal?.status == StatusType.CLOSE || _journal?.status == StatusType.SUSPEND;
  String get lockMessage => _journal?.status == StatusType.CLOSE ? "JOURNAL CLOSED" : "JOURNAL SUSPENDED";

  Future<void> loadJournal({
    required ServerConfigProvider serverConfig,
    required ClientConfigProvider clientConfig,
    required SecurityProvider security,
  }) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await MessengerService.sendRequest<JournalDTO>(
        serverIp: serverConfig.serverIp!,
        serverPort: serverConfig.serverPort!,
        service: NetworkConstants.journalService,
        action: NetworkConstants.journalGetAction,
        payload: {"pin": security.systemPin, "deviceId": clientConfig.deviceId},
        fromJson: (json) => JournalDTO.fromJson(json),
      );

      _journal = response;
      notifyListeners();
    } catch (e) {
      debugPrint("Journal Load Error: $e");
      rethrow; // Rethrow so the UI can show the "Fatal Error" dialog
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  void updateJournal(JournalDTO newJournal) {
    _journal = newJournal;
    notifyListeners(); // This triggers the UI overlay to appear/disappear
  }

  void clear() {
    _journal = null;
    notifyListeners();
  }
}
