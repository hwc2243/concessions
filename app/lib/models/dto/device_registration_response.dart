class DeviceRegistrationResponseDTO {
  final String deviceNumber;

  DeviceRegistrationResponseDTO({required this.deviceNumber});

  /// Factory to create the DTO from JSON returned by the Java server
  factory DeviceRegistrationResponseDTO.fromJson(Map<String, dynamic> json) {
    return DeviceRegistrationResponseDTO(
      deviceNumber: json['deviceNumber'] ?? '',
    );
  }
}