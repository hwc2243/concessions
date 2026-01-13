package com.concessions.android

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.isVisible
import com.concessions.android.order.OrderScreen
import com.concessions.android.order.OrderViewModel
import com.concessions.android.ui.theme.POSTheme
import com.concessions.dto.JournalDTO
import com.concessions.dto.MenuDTO
import com.concessions.dto.OrderDTO
import com.concessions.dto.OrderItemDTO
import com.concessions.common.event.JournalListener
import com.concessions.common.network.MessengerException
import com.concessions.common.network.NetworkConstants
import com.concessions.common.network.RegistrationClient
import com.concessions.common.network.dto.ConfigurationResponseDTO
import com.concessions.common.network.dto.DeviceRegistrationRequestDTO
import com.concessions.common.network.dto.DeviceRegistrationResponseDTO
import com.concessions.common.network.dto.OrderRequestDTO
import com.concessions.common.network.dto.PINVerifyRequestDTO
import com.concessions.common.network.dto.SimpleDeviceRequestDTO
import com.concessions.common.network.dto.SimpleResponseDTO
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime;
import java.util.UUID

sealed class ScreenState {
    object Discovering : ScreenState()
    object PinEntry : ScreenState()
    data class VerifyingPin(val pin: String) : ScreenState()
    object RegisteringDevice : ScreenState()
    object FetchingConfiguration : ScreenState()
    object ProcessingCheckout : ScreenState()
    object OrderEntry : ScreenState()
    data class Error(val message: String, val isFatal: Boolean = true) : ScreenState()
}

data class OverlayState(val isVisible: Boolean = false, val message: String = "")

class MainActivity : ComponentActivity() {

    private val LOGTAG = "MainActivity"
    private val PREFS_NAME = "POSPreferences"
    private val DEVICE_ID_PREF = "deviceId"
    private val PIN_PREF = "pin" // Added for PIN logic

    private val viewModel: MainViewModel by viewModels()

    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val mapper = jacksonObjectMapper().apply {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }

        // Setup Device ID (runs once)
        var deviceId = prefs.getString(DEVICE_ID_PREF, null)
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString(DEVICE_ID_PREF, deviceId).apply()
            Log.i(LOGTAG, "New Device ID generated and saved: $deviceId")
        } else {
            Log.i(LOGTAG, "Using existing DeviceId: $deviceId")
        }
        viewModel.deviceContext.deviceId = deviceId


        setContent {
            POSTheme {
                var screenState by remember { mutableStateOf<ScreenState>(ScreenState.Discovering) }
                var overlayState by remember { mutableStateOf(OverlayState()) }
                var retryTrigger by remember { mutableStateOf(0) }
                val scope = rememberCoroutineScope()

                DisposableEffect(Unit) {
                    val journalListener = object : JournalListener {
                        override fun journalClosed(journal: JournalDTO) {
                            Log.i(LOGTAG, "Journal network event: CLOSED")
                            viewModel.setJournal(journal)
                            overlayState = OverlayState(isVisible = true, message = "Journal is Closed")
                        }

                        override fun journalChanged(journal: JournalDTO) {
                            Log.i(LOGTAG, "Journal network event: CHANGED")
                            viewModel.setJournal(journal)
                        }

                        override fun journalOpened(journal: JournalDTO) {
                            Log.i(LOGTAG, "Journal network event: OPENED")
                            viewModel.setJournal(journal)
                            overlayState = OverlayState(isVisible = false)
                        }

                        override fun journalStarted(journal: JournalDTO) {
                            Log.i(LOGTAG, "Journal network event: STARTED")
                            viewModel.setJournal(journal)
                        }

                        override fun journalSuspended(journal: JournalDTO) {
                            Log.i(LOGTAG, "Journal network event: SUSPENDED")
                            viewModel.setJournal(journal)
                            overlayState = OverlayState(isVisible = true, message = "Journal is Suspended")
                        }

                        override fun journalSynced(journal: JournalDTO) {
                            Log.i(LOGTAG, "Journal network event: SYNCED")
                            viewModel.setJournal(journal)
                        }
                    }

                    viewModel.journalNotifier.addJournalListener(journalListener)

                    // onDispose is called when the composable leaves the screen
                    onDispose {
                        viewModel.journalNotifier.removeJournalListener(journalListener)
                    }
                }

                // This effect runs the discovery logic and updates the state
                LaunchedEffect(retryTrigger) {
                    if (screenState is ScreenState.Discovering) {
                        Log.d(LOGTAG, "Starting server discovery...")
                        val wifiManager =
                            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        val multicastLock = wifiManager.createMulticastLock("discoveryLock")

                        try {
                            multicastLock.acquire()
                            Log.d(LOGTAG, "Multicast lock acquired.")

                            val response = withContext(Dispatchers.IO) {
                                RegistrationClient(mapper).discoverService()
                            }
                            if (response == null) {
                                screenState = ScreenState.Error("Failed to locate server")
                                Log.e(LOGTAG, "discoverService() returned null.")
                            } else {
                                Log.i(
                                    LOGTAG,
                                    "Server discovered at ${response.serverIp}:${response.serverPort}"
                                )
                                viewModel.createMessenger(mapper, response.serverIp, response.serverPort)
                                val existingPin = prefs.getString(PIN_PREF, null)
                                screenState = if (existingPin == null) {
                                    ScreenState.PinEntry
                                } else {
                                    ScreenState.VerifyingPin(existingPin)
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(LOGTAG, "Discovery failed", e)
                            screenState = ScreenState.Error("Server discovery failed: ${e.message}")
                        } finally {
                            if (multicastLock.isHeld) {
                                multicastLock.release()
                                Log.d(LOGTAG, "Multicast lock released.")
                            }
                        }
                    }
                }

                // This effect verifies the PIN and if valid starts the network listener
                LaunchedEffect(screenState) {
                    if (screenState is ScreenState.VerifyingPin) {
                        val pin = (screenState as ScreenState.VerifyingPin).pin
                        Log.d(LOGTAG, "Verifying PIN...")
                        val messenger = viewModel.messenger
                        if (messenger == null) {
                            screenState = ScreenState.Error("Messenger not available.")
                            return@LaunchedEffect
                        }
                        try {
                            val response = withContext(Dispatchers.IO) {
                                messenger.sendRequest(
                                    NetworkConstants.PIN_SERVICE,
                                    NetworkConstants.PIN_VERIFY_ACTION,
                                    PINVerifyRequestDTO(pin),
                                    SimpleResponseDTO::class.java
                                )
                            }

                            // This code is only reached if the network call was successful.
                            Log.i(LOGTAG, "PIN verified successfully: ${response.message}")
                            viewModel.deviceContext.pin = pin

                            // If PIN is new, save it.
                            if (prefs.getString(PIN_PREF, null) == null) {
                                prefs.edit().putString(PIN_PREF, pin).apply()
                                Log.i(LOGTAG, "New PIN saved to preferences.")
                            }
                            Log.i(LOGTAG, "Starting local network listener...")
                            viewModel.createLocalNetworkListener(mapper)
                            screenState = ScreenState.RegisteringDevice
                        } catch (e: Exception) {
                            Log.e(LOGTAG, "PIN verification failed with exception", e)
                            val isConnectionError = e is java.net.ConnectException || e.cause is java.net.ConnectException

                            if (isConnectionError) {
                                // Transition to Error state, flagged as fatal so retry goes to Discovering
                                screenState = ScreenState.Error(
                                    message = "Connection to server lost",
                                    isFatal = true
                                )
                            } else {
                                // For actual PIN mismatches or logic errors, clear the saved PIN and show error
                                prefs.edit().remove(PIN_PREF).apply()
                                screenState = ScreenState.Error("PIN verification failed: ${e.message}")
                            }
                        }
                    }
                }

                // This effect registers the device
                LaunchedEffect(screenState) {
                    if (screenState is ScreenState.RegisteringDevice) {
                        Log.d(LOGTAG, "Registering device...")
                        val messenger = viewModel.messenger
                        if (messenger == null) {
                            screenState = ScreenState.Error("Messenger not available.")
                            return@LaunchedEffect
                        }
                        val localNetworkListener = viewModel.localNetworkListener
                        if (localNetworkListener == null) {
                            screenState = ScreenState.Error("Local network listener not available.")
                            return@LaunchedEffect
                        }
                        viewModel.deviceContext.deviceType = "POS"
                        viewModel.deviceContext.deviceIp = localNetworkListener.getListenerIp()
                        viewModel.deviceContext.devicePort = localNetworkListener.getListenerPort()

                        try {
                            val response = withContext(Dispatchers.IO) {
                                val deviceRegistrationRequest = DeviceRegistrationRequestDTO(
                                    viewModel.deviceContext.pin!!,
                                    viewModel.deviceContext.deviceId!!,
                                    viewModel.deviceContext.deviceType!!,
                                    viewModel.deviceContext.deviceIp!!,
                                    viewModel.deviceContext.devicePort!!)

                                messenger.sendRequest(
                                    NetworkConstants.DEVICE_SERVICE,
                                    NetworkConstants.DEVICE_REGISTER_ACTION,
                                    deviceRegistrationRequest,
                                    DeviceRegistrationResponseDTO::class.java
                                )
                            }
                            viewModel.deviceContext.deviceNumber = response?.deviceNumber
                            Log.i(LOGTAG, "Device registered successfully: ${response.deviceNumber}")
                            screenState = ScreenState.FetchingConfiguration
                        } catch (e: MessengerException) {
                            Log.e(LOGTAG, "Device registration failed", e)
                            val isConnectionError = e is java.net.ConnectException || e.cause is java.net.ConnectException

                            if (isConnectionError) {
                                // Transition to Error state, flagged as fatal so retry goes to Discovering
                                screenState = ScreenState.Error(
                                    message = "Connection to server lost",
                                    isFatal = true
                                )
                            } else {
                                screenState = ScreenState.Error("Device registration failed: ${e.message}")// For actual PIN mismatches or logic errors, clear the saved PIN and show error
                            }
                        }
                    }
                }

                // This Effect retrieves the location configuration
                LaunchedEffect(screenState) {
                    if (screenState is ScreenState.FetchingConfiguration) {
                        Log.d(LOGTAG, "Fetching configuration...")
                        val messenger = viewModel.messenger
                        if (messenger == null) {
                            screenState = ScreenState.Error("Messenger not available.")
                            return@LaunchedEffect
                        }
                        try {
                            var deviceRequest = SimpleDeviceRequestDTO()
                            deviceRequest.setPIN(viewModel.deviceContext.pin!!)
                            deviceRequest.setDeviceId(viewModel.deviceContext.deviceId!!)

                            val response = withContext(Dispatchers.IO) {
                                messenger.sendRequest(
                                    NetworkConstants.CONFIGURATION_SERVICE,
                                    NetworkConstants.CONFIGURATION_LOCATION_ACTION,
                                    deviceRequest,
                                    ConfigurationResponseDTO::class.java
                                )
                            }
                            if (response == null) {
                                Log.e(LOGTAG, "Configuration response was null.")
                                screenState = ScreenState.Error("No configuration available", isFatal = false)
                                return@LaunchedEffect
                            }
                            viewModel.locationContext.organizationName = response?.organizationName
                            viewModel.locationContext.locationName = response?.locationName
                            viewModel.locationContext.menuName = response?.menuName
                            Log.i(LOGTAG, "Configuration fetched successfully.")

                            val menuResponse = withContext(Dispatchers.IO) {
                                messenger.sendRequest(
                                    NetworkConstants.MENU_SERVICE,
                                    NetworkConstants.MENU_GET_ACTION,
                                    deviceRequest,
                                    MenuDTO::class.java
                                )
                            }
                            if (menuResponse == null) {
                                Log.e(LOGTAG, "Menu response was null.")
                                screenState = ScreenState.Error("No menu available", isFatal = false)
                                return@LaunchedEffect
                            }
                            viewModel.setMenu(menuResponse)
                            menuResponse?.let { orderViewModel.setMenu(it) }
                            Log.i(LOGTAG, "Menu fetched successfully.")

                            val journalResponse = withContext(Dispatchers.IO) {
                                messenger.sendRequest(
                                    NetworkConstants.JOURNAL_SERVICE,
                                    NetworkConstants.JOURNAL_GET_ACTION,
                                    deviceRequest,
                                    JournalDTO::class.java
                                )
                            }
                            if (journalResponse == null) {
                                Log.e(LOGTAG, "Journal response was null.")
                                screenState = ScreenState.Error("No journal available", isFatal = false)
                                return@LaunchedEffect
                            }
                            viewModel.setJournal(journalResponse)
                            Log.i(LOGTAG, "Journal fetched successfully.")

                            screenState = ScreenState.OrderEntry
                        } catch (e: Exception) {
                            val isConnectionError = e is java.net.ConnectException || e.cause is java.net.ConnectException

                            if (isConnectionError) {
                                // Transition to Error state, flagged as fatal so retry goes to Discovering
                                screenState = ScreenState.Error(
                                    message = "Connection to server lost",
                                    isFatal = true
                                )
                            } else {
                                screenState = ScreenState.Error("Failed to fetch configuration: ${e.message}")
                            }
                        }
                    }
                }

                // This effect ensures the overlay status is correct whenever we are in the OrderEntry state
                LaunchedEffect(screenState) {
                    if (screenState is ScreenState.OrderEntry) {
                        viewModel.journal.value?.let {
                            viewModel.journalNotifier.publishJournalStatus(it)
                        }
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        // Use a 'when' statement to display the correct UI for the current state
                        when (val state = screenState) {
                            is ScreenState.Discovering -> LoadingUI("Looking for server")
                            is ScreenState.PinEntry -> PinEntryScreen(onPinSet = { newPin ->
                                screenState = ScreenState.VerifyingPin(newPin)
                            })
                            is ScreenState.VerifyingPin -> LoadingUI("Verifying PIN...")
                            is ScreenState.RegisteringDevice -> LoadingUI("Registering device...")
                            is ScreenState.FetchingConfiguration -> LoadingUI("Fetching configuration...")
                            is ScreenState.ProcessingCheckout -> LoadingUI("Processing Checkout...")
                            is ScreenState.OrderEntry -> {
                                Box(Modifier.fillMaxSize()) {
                                    OrderScreen(
                                        orderViewModel = orderViewModel,
                                        onCheckout = {
                                            scope.launch {
                                                val currentOrderItems =
                                                    orderViewModel.uiState.value.currentOrderItems
                                                if (currentOrderItems.isEmpty()) {
                                                    Log.i(
                                                        LOGTAG,
                                                        "Checkout clicked with empty order."
                                                    )
                                                    return@launch // Maybe show a toast message
                                                }

                                                val order = OrderDTO().apply {
                                                    journalId = viewModel.journal.value?.id
                                                    orderTotal =
                                                        orderViewModel.uiState.value.orderTotal
                                                    menuId = viewModel.menu.value?.id
                                                    startTs = LocalDateTime.now()

                                                    val items = currentOrderItems.map { menuItem ->
                                                        OrderItemDTO().apply {
                                                            menuItemId = menuItem.id
                                                            name = menuItem.name
                                                            price = menuItem.price
                                                        }
                                                    }
                                                    this.orderItems = items.toMutableList()
                                                }

                                                screenState = ScreenState.ProcessingCheckout
                                                try {
                                                    var orderRequest = OrderRequestDTO()
                                                    orderRequest.setPIN(viewModel.deviceContext.pin!!)
                                                    orderRequest.setDeviceId(viewModel.deviceContext.deviceId!!)
                                                    orderRequest.setOrder(order);
                                                    withContext(Dispatchers.IO) {
                                                        viewModel.messenger?.sendRequest(
                                                            NetworkConstants.ORDER_SERVICE,
                                                            NetworkConstants.ORDER_SUBMIT_ACTION,
                                                            orderRequest,
                                                            SimpleResponseDTO::class.java
                                                        )
                                                    }
                                                    orderViewModel.clearOrder()
                                                    Log.i(LOGTAG, "Checkout successful.")
                                                    screenState = ScreenState.OrderEntry
                                                } catch (e: Exception) {
                                                    Log.e(LOGTAG,"Checkout failed",e)
                                                    screenState = ScreenState.Error("Checkout failed: ${e.message}")
                                                }
                                            }
                                        }
                                    )
                                    // Display the overlay on top if it's visible
                                    if (overlayState.isVisible) {
                                        DisabledOverlay(message = overlayState.message)
                                    }
                                }
                            }
                            is ScreenState.Error -> ErrorUI(
                                message = state.message,
                                onRetry = {
                                    if (state.isFatal) {
                                        // For fatal errors, restart the whole discovery process
                                        screenState = ScreenState.Discovering
                                        retryTrigger++
                                 } else {
                                        // For non-fatal, just re-trigger the current state
                                        screenState = ScreenState.FetchingConfiguration
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DisabledOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            // Gray background with 70% opacity
            .background(Color.Black.copy(alpha = 0.7f))
            // Consume all clicks to prevent interaction with the screen below
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
// Helper composable for loading states
@Composable
fun LoadingUI(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
        Text(text = message, modifier = Modifier.padding(top = 16.dp))
    }
}

// Helper composable for the error state
@Composable
fun ErrorUI(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = message)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}