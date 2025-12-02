package at.htlwels.jetpackble

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import java.util.UUID

class BleViewModel(application: Application) : AndroidViewModel(application) {

    var currentScreen = 0

    val names = mutableStateListOf<String>()
    val devices = mutableStateListOf<BluetoothDevice>()
    var data: String by mutableStateOf("")
        private set

    var mode by mutableStateOf(1)
    var suspension by mutableStateOf(0)

    //using StateFlow to hold UI state
    private val _flowData = MutableStateFlow("")
    val flowData: StateFlow<String> = _flowData.asStateFlow()

    //testing navigation
    private val _nav = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val nav = _nav.asSharedFlow()

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    //Generic Attribute Profile
    private var bluetoothGatt: BluetoothGatt? = null

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    init {
        // Safely get the BluetoothManager from the application context
        bluetoothManager = application.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    }

    fun changeMode(m : Int) {
        mode = m;
    }

    fun changeSuspension(s : Int) {
        suspension = s;
    }

    @Throws(SecurityException::class)
    fun isDeviceConnected(device: BluetoothDevice?): Boolean {
        val connectedDevices = bluetoothManager?.getConnectedDevices(BluetoothProfile.GATT)
        if (connectedDevices != null) {
            for (connectedDevice in connectedDevices) {
                if (connectedDevice == device) {
                    return true
                }
            }
        }
        return false
    }

    @Throws(SecurityException::class)
    fun disconnectDevice() {
        if (bluetoothGatt != null) {
            bluetoothGatt!!.disconnect()
            bluetoothGatt!!.close()
            bluetoothGatt = null
            _flowData.value += "\nDisconnected from device!\n"
        }
    }

    fun connect(bd : BluetoothDevice) {
        try {
            if (isDeviceConnected(bd)) {
                disconnectDevice();
            } else {
                //connect profil
                bluetoothGatt = bd.connectGatt(application.applicationContext, false, gattCallback);
                _flowData.value += "\nTrying to connect ...\n"
            }
            //bluetoothLeScanner.stopScan(leScanCallback);
        } catch (e: SecurityException) {

        }
    }

    fun scanLeDevice() {
        if (ContextCompat.checkSelfPermission(application.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            //we have no permission!
        }
        else {
            if (!scanning) { // Stops scanning after a pre-defined scan period.
                names.clear()
                devices.clear()
                handler.postDelayed({
                    scanning = false
                    //safe call - call methode only if object != null
                    bluetoothLeScanner?.stopScan(leScanCallback)
                }, SCAN_PERIOD)
                scanning = true
                bluetoothLeScanner?.startScan(leScanCallback)
            } else {
                scanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }
        }
    }

    //**********************************************************************************************************************
    //SCAN CALLBACK
    //**********************************************************************************************************************
    private val leScanCallback = object : ScanCallback() {
        @Throws(SecurityException::class)
        private fun dealWithResult(result: ScanResult) {
            if (result.getDevice().getName() != null) {
                val name = result.getDevice().getName()
                if (!names.contains(name) && !name.lowercase(Locale.getDefault())
                        .contains("unknown")
                ) {
                    names.add(name)
                    devices.add(result.getDevice())
                }
            }
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            //!! -> result has to be != null otherwise throw exception
            dealWithResult(result!!)
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {
            super.onBatchScanResults(results)
            results?.forEach { result ->
                dealWithResult(result!!)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
        }
    }

    //**********************************************************************************************************************
    //GATT CALLBACK
    //**********************************************************************************************************************
    //working for MLT-BT05 BLE4.0
    val YOUR_SERVICE_UUID: UUID = UUID.fromString("000ffe0-0000-1000-8000-00805f9b34fb")
    val YOUR_CHARACTERISTIC_UUID: UUID = UUID.fromString("000ffe1-0000-1000-8000-00805f9b34fb")

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _flowData.value += "\nConnected to device!\n"
                try {
                    gatt.discoverServices()
                } catch (e: SecurityException) {
                    //missing permission!
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //disconnected
                _flowData.value += "\nDisconnected from device!\n"
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(YOUR_SERVICE_UUID)
                val characteristic = service.getCharacteristic(YOUR_CHARACTERISTIC_UUID)

                //TODO: check if service/characteristic != null
                try {
                    gatt.setCharacteristicNotification(characteristic, true)
                } catch (e: SecurityException) {
                    //missing permission!
                    //TODO: notify user
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Handle the characteristic read
                val data = characteristic.getValue()
                // Process the data
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic) {
            if (YOUR_CHARACTERISTIC_UUID == characteristic.getUuid()) {
                val d = characteristic.getValue()
                val receivedData = String(d)

                data += receivedData
                if(data.length > 1000) {
                    data = ""
                }

                _flowData.value += receivedData
                if(_flowData.value.length > 1000) {
                    _flowData.value = ""
                }

                //navigation test
                if(receivedData.contains("0") && currentScreen != 0) {
                    currentScreen = 0
                    _nav.tryEmit("home")
                }
                else if(receivedData.contains("1") && currentScreen != 1) {
                    currentScreen = 1
                    _nav.tryEmit("p_0")
                }
                else if(receivedData.contains("2") && currentScreen != 2) {
                    currentScreen = 2
                    _nav.tryEmit("p_1")
                }
                else if(receivedData.contains("3") && currentScreen != 3) {
                    currentScreen = 3
                    _nav.tryEmit("p_2")
                }
                else if(receivedData.contains("4") && currentScreen != 4) {
                    currentScreen = 4
                    _nav.tryEmit("p_3")
                }
                else if(receivedData.contains("5") && currentScreen != 5) {
                    currentScreen = 5
                    _nav.tryEmit("p_4")
                }
            }
        }
    }
}