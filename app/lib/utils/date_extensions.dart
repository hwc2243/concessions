import 'package:intl/intl.dart'; // Optional: if you use the intl package

extension DateTimeExtension on DateTime {
  /// Converts to ISO-8601 but removes fractional seconds (millis/micros)
  /// Result: "2026-02-16T11:08:17"
  String toJavaSafeIsoString() {
    String iso = this.toIso8601String();
    // Splitting at the dot removes everything after the seconds
    return iso.split('.').first;
  }
}