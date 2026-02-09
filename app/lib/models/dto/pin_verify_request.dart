class PINVerifyRequestDTO {
  final String pin;

  PINVerifyRequestDTO({required this.pin});

  Map<String, dynamic> toJson() => {'pin': pin};
}