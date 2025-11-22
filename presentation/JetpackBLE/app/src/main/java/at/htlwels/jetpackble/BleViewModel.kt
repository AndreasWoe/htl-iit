package at.htlwels.jetpackble

import android.Manifest
import android.app.Activity
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import java.util.Locale
import java.util.UUID

class BleViewModel(application: Application) : AndroidViewModel(application) {

    val names = mutableStateListOf<String>()
    val devices = mutableStateListOf<BluetoothDevice>()
    var data: String by mutableStateOf("")
        private set

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null

    //Generic Attribute Profile
    private var bluetoothGatt: BluetoothGatt? = null

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    fun initBLE() {
        // Safely get the BluetoothManager from the application context
        bluetoothManager = application.getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
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
            //Snackbar.make(findViewById(R.id.loMain), "Disconnected!", Snackbar.LENGTH_LONG).show()
        }
    }

    fun connect(bd : BluetoothDevice) {
        try {
            if (isDeviceConnected(bd)) {
                disconnectDevice();
            } else {
                //connect profil
                bluetoothGatt = bd.connectGatt(application.applicationContext, false, gattCallback);
                //Snackbar.make(findViewById(R.id.loMain), "Trying to connect ...", Snackbar.LENGTH_LONG).show();
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
                //Snackbar.make(findViewById(R.id.loMain), "Connected!", Snackbar.LENGTH_LONG).show()
                try {
                    gatt.discoverServices()
                } catch (e: SecurityException) {
                    //missing permission!
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //disconnected
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
            }
        }
    }
}