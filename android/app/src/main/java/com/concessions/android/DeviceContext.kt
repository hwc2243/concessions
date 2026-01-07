package com.concessions.android

data class DeviceContext (
    var pin: String? = null,
    var deviceId: String? = null,
    var deviceNumber: String? = null,
    var deviceType: String? = null,
    var deviceIp: String? = null,
    var devicePort: Int? = null
)