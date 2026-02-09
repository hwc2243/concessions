class SimpleResponseDTO {
  final String? message;
  bool get isSuccess => message?.toUpperCase() == "SUCCESS";

  SimpleResponseDTO({required this.message});

  factory SimpleResponseDTO.fromJson(Map<String, dynamic> json) {
    return SimpleResponseDTO(
      message: json['message'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'message': message,
    };
  }
}