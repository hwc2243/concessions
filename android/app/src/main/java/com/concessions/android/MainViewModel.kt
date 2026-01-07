package com.concessions.android

import androidx.lifecycle.ViewModel
import com.concessions.dto.MenuDTO
import com.concessions.common.network.HealthCheckManager
import com.concessions.common.network.LocalNetworkListener
import com.concessions.common.network.ManagerRegistry
import com.concessions.common.network.Messenger
import com.fasterxml.jackson.databind.ObjectMapper;

class MainViewModel : ViewModel() {
    val deviceContext = DeviceContext()

    val locationContext = LocationContext()

    // This will hold the single instance of the Messenger
    var messenger: Messenger? = null
        private set // Only the ViewModel can set it

    var localNetworkListener: LocalNetworkListener? = null
        private set

    var menu: MenuDTO? = null
        private set

    fun setMenu(newMenu: MenuDTO?) {
        this.menu = newMenu
    }

    fun createMessenger(mapper: ObjectMapper, serverIp: String, serverPort: Int) {
        // Create the Messenger instance only if it doesn't already exist
        if (messenger == null) {
            messenger = Messenger(mapper, serverIp, serverPort)
        }
    }

    fun createLocalNetworkListener(mapper: ObjectMapper) {
        if (localNetworkListener == null) {
            var managerRegistry = ManagerRegistry()
            var healthCheckManager = HealthCheckManager(mapper)
            healthCheckManager?.register()
            localNetworkListener = LocalNetworkListener(managerRegistry, mapper)
            localNetworkListener?.start()
        }
    }
    // The ViewModel is cleared when the Activity is destroyed.
    // We can add cleanup logic here if the Messenger needs it.
    override fun onCleared() {
        super.onCleared()
        localNetworkListener?.shutdown()
    }
}
