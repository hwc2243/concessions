class NetworkConstants {
  // To prevent the class from being instantiated
  NetworkConstants._();

  // Configuration service
  static const String configurationService = "CONFIG";
  static const String configurationLocationAction = "LOCATION";

  // Device service
  static const String deviceService = "DEVICE";
  static const String deviceRegisterAction = "REGISTER";

  // Health Check Service
  static const String healthService = "HEALTH";
  static const String healthCheckAction = "CHECK";

  // Journal Service
  static const String journalService = "JOURNAL";
  static const String journalGetAction = "GET";
  static const String journalChangeAction = "CHANGE";

  // Menu service
  static const String menuService = "MENU";
  static const String menuGetAction = "GET";

  // Order Service
  static const String orderService = "ORDER";
  static const String orderCompleteAction = "COMPLETE";
  static const String orderCompletedAction = "COMPLETED";
  static const String orderCreatedAction = "CREATED";
  static const String orderGetallAction = "GETALL";
  static const String orderSubmitAction = "SUBMIT";

  // PIN Service
  static const String pinService = "PIN";
  static const String pinVerifyAction = "VERIFY";
}