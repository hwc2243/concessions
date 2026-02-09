class DeviceRegistrationRequestDTO {
  final String pin;
  final String deviceId;
  final String deviceType;
  final String deviceIp;
  final int devicePort;

  DeviceRegistrationRequestDTO({
    required this.pin,
    required this.deviceId,
    required this.deviceType,
    required this.deviceIp,
    required this.devicePort,
  });

  /// Converts the DTO to a Map for JSON serialization
  Map<String, dynamic> toJson() {
    return {
      'pin': pin,
      'deviceId': deviceId,
      'deviceType': deviceType,
      'deviceIp': deviceIp,
      'devicePort': devicePort,
    };
  }
}