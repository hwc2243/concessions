package com.concessions.android

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.concessions.android.order.OrderScreen
import com.concessions.android.order.OrderViewModel
import com.concessions.android.ui.theme.POSTheme
import com.concessions.dto.MenuDTO
import com.concessions.common.network.MessengerException
import com.concessions.common.network.NetworkConstants
import com.concessions.common.network.RegistrationClient
import com.concessions.common.network.dto.ConfigurationResponseDTO
import com.concessions.common.network.dto.DeviceRegistrationRequestDTO
import com.concessions.common.network.dto.DeviceRegistrationResponseDTO
import com.concessions.common.network.dto.PINVerifyRequestDTO
import com.concessions.common.network.dto.SimpleDeviceRequestDTO
import com.concessions.common.network.dto.SimpleResponseDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class ScreenState {
    object Discovering : ScreenState()
    object PinEntry : ScreenState()
    data class VerifyingPin(val pin: String) : ScreenState()
    object RegisteringDevice : ScreenState()
    object FetchingConfiguration : ScreenState()
    object OrderEntry : ScreenState()
    data class Error(val message: String) : ScreenState()
}

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
        val mapper = jacksonObjectMapper()

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
                var retryTrigger by remember { mutableStateOf(0) }

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
                            prefs.edit().remove(PIN_PREF).apply()
                            screenState = ScreenState.Error("PIN verification failed: ${e.message}")
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
                            screenState = ScreenState.Error("Device registration failed: ${e.message}")
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

                            viewModel.locationContext.organizationName = response?.organizationName
                            viewModel.locationContext.locationName = response?.locationName
                            viewModel.locationContext.menuName = response?.menuName
                            Log.i(LOGTAG, "Location Configuration fetched successfully.")

                            val menuResponse = withContext(Dispatchers.IO) {
                                messenger.sendRequest(
                                    NetworkConstants.MENU_SERVICE,
                                    NetworkConstants.MENU_GET_ACTION,
                                    deviceRequest,
                                    MenuDTO::class.java
                                )
                            }
                            viewModel.setMenu(menuResponse)

                            menuResponse?.let { orderViewModel.setMenu(it) }

                            screenState = ScreenState.OrderEntry
                        } catch (e: Exception) {
                            screenState = ScreenState.Error("Failed to fetch configuration: ${e.message}")
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
                            is ScreenState.OrderEntry -> OrderScreen(orderViewModel = orderViewModel)
                            is ScreenState.Error -> ErrorUI(
                                message = state.message,
                                onRetry = {
                                    // Reset to the initial state to trigger discovery again
                                    screenState = ScreenState.Discovering
                                    retryTrigger++
                                }
                            )
                        }
                    }
                }
            }
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
// The old Greeting composable is no longer necessary as Text() is used directly.
// You can remove it or keep it if you plan to add more styling later.

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    POSTheme {
        Text("Hello Android!")
    }
}
