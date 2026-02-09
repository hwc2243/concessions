import 'dart:convert';
import '/providers/journal_provider.dart';
import '/models/dto/journal_dto.dart';
import '/models/dto/simple_response.dart';
import '/network/network_constants.dart';
import 'base_handler.dart';

class JournalClientHandler extends BaseHandler {
  final JournalProvider journalProvider;

  JournalClientHandler(this.journalProvider);

  @override
  String get name => NetworkConstants.journalService;

  @override
  Future<dynamic> process(String action, String payload) async {
    if (action == NetworkConstants.journalChangeAction) {
      final json = jsonDecode(payload);
      final journal = JournalDTO.fromJson(json);
      
      // Update the provider state globally
      journalProvider.updateJournal(journal);
      
      return SimpleResponseDTO(message: "Status updated to ${journal.status.name}");
    }
    throw Exception("Action $action not implemented");
  }
}