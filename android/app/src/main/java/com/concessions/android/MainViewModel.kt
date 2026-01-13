package com.concessions.android

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.concessions.dto.JournalDTO
import com.concessions.dto.MenuDTO
import com.concessions.common.event.JournalNotifier
import com.concessions.common.network.HealthCheckHandler
import com.concessions.common.network.JournalClientHandler
import com.concessions.common.network.LocalNetworkListener
import com.concessions.common.network.HandlerRegistry
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

    val menu = mutableStateOf<MenuDTO?>(null)

    val journal = mutableStateOf<JournalDTO?>(null)

    val journalNotifier = JournalNotifier()

    fun setMenu(newMenu: MenuDTO?) {
        this.menu.value = newMenu
    }

    fun setJournal(newJournal: JournalDTO?) {
        this.journal.value = newJournal
    }

    // This method will create)
    fun createMessenger(mapper: ObjectMapper, serverIp: String, serverPort: Int) {
        // Create the Messenger instance only if it doesn't already exist
        if (messenger == null) {
            messenger = Messenger(mapper, serverIp, serverPort)
        }
    }

    fun createLocalNetworkListener(mapper: ObjectMapper) {
        if (localNetworkListener == null) {
            val handlerRegistry = HandlerRegistry()
            val healthCheckManager = HealthCheckHandler(mapper)
            healthCheckManager?.register()
            val journalClientManager = JournalClientHandler(mapper,journalNotifier);
            journalClientManager?.register()
            localNetworkListener = LocalNetworkListener(handlerRegistry, mapper)
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
