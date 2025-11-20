package at.htlwels.btlescanner;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnItemClickListener {
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private StringBuffer inBuffer = new StringBuffer();
    private int inBuffer_Start = -1;

    //Generic Attribute Profile
    private BluetoothGatt bluetoothGatt;

    ArrayList<String> names = new ArrayList<>();
    List<BluetoothDevice> deviceList = new ArrayList<>();

    MyAdapter adapter;

    private Handler handler = new Handler();
    private Runnable runnable;
    private int index = 0;

    private ImageView imageView;
    private int[] images = {
            R.drawable.pcm_1,
            R.drawable.pcm_2,
            R.drawable.pcm_3,
            R.drawable.pcm_4,
            R.drawable.pcm_main_0
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        //ActionBar actionBar = getActionBar();
        //actionBar.hide();

        TextView textView = findViewById(R.id.txtConsole);
        textView.setMovementMethod(new ScrollingMovementMethod());

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Set LayoutManager
        RecyclerView recyclerView = findViewById(R.id.rvDiscoveryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set Adapter
        adapter = new MyAdapter(deviceList, this);
        recyclerView.setAdapter(adapter);

        //recycle view add demo items
        /*
        for(int i=0; i<100; i++) {
            String macAddress = "00:11:22:33:44:55";
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
            deviceList.add(device);
        }
        */
        adapter.notifyDataSetChanged();
    }

    public void onConsoleClearClick(View v) {
        TextView txtConsole = findViewById(R.id.txtConsole);
        txtConsole.setText("");
    }

    public void btnDemoClick(View v) {
        ImageView imageView = findViewById(R.id.ivMain);

        runnable = new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(images[index]);
                index++;
                if(index < 5)
                    handler.postDelayed(this, 500); // 200 ms delay
                else
                    index = 0;
            }
        };

        handler.post(runnable); // start animation
    }

    public void btnScanOnClick(View view) {
        //test
        //ImageView iv = findViewById(R.id.ivMain);
        //iv.setImageResource(R.drawable.pcm_main_0);

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
            Snackbar.make(findViewById(R.id.loMain), "Bluetooth disabled!", Snackbar.LENGTH_LONG).show();
        else
            startScanning();

        TextView tvConsole = findViewById(R.id.txtConsole);
        tvConsole.setText("Scanning ...");

        //show device list
        RecyclerView rv = findViewById(R.id.rvDiscoveryList);
        rv.setVisibility(VISIBLE);
    }

    private void startScanning() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        else {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            try {
                bluetoothLeScanner.startScan(leScanCallback);
                // Stop the scan after 10 seconds
                new Handler().postDelayed(() -> {
                    bluetoothLeScanner.stopScan(leScanCallback);
                    Snackbar.make(findViewById(R.id.loMain), "Scan finished!", Snackbar.LENGTH_LONG).show();
                }, 10000); // 10000 milliseconds = 10 seconds
            } catch (SecurityException e) {
                //missing permission!
                //TODO: notify user
            }
        }
    }

    //**********************************************************************************************************************
    //GATT CALLBACK
    //**********************************************************************************************************************
    //working for MLT-BT05 BLE4.0
    private static final UUID YOUR_SERVICE_UUID = UUID.fromString("000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID YOUR_CHARACTERISTIC_UUID = UUID.fromString("000ffe1-0000-1000-8000-00805f9b34fb");

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Snackbar.make(findViewById(R.id.loMain), "Connected!", Snackbar.LENGTH_LONG).show();
                try {
                    gatt.discoverServices();
                } catch (SecurityException e) {
                    Snackbar.make(findViewById(R.id.loMain), "Missing permission!", Snackbar.LENGTH_LONG).show();
                }
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                Snackbar.make(findViewById(R.id.loMain), "Disconnected!", Snackbar.LENGTH_LONG).show();

            //hide device list
            RecyclerView rv = findViewById(R.id.rvDiscoveryList);
            rv.setVisibility(INVISIBLE);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                //discover all services and characteristics
                /*
                for (BluetoothGattService service : gatt.getServices()) {
                    TextView tv = findViewById(R.id.txtConsole);
                    String s = tv.getText().toString();
                    s += "Service UUID: " + service.getUuid().toString();
                    s += System.lineSeparator();
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        s += "Characteristic UUID: " + characteristic.getUuid().toString();
                        s += System.lineSeparator();
                    }
                    tv.setText(s);
                }
                */

                BluetoothGattService service = gatt.getService(YOUR_SERVICE_UUID);
                BluetoothGattCharacteristic characteristic = service.getCharacteristic(YOUR_CHARACTERISTIC_UUID);

                //TODO: check if service/characteristic != null
                try {
                    gatt.setCharacteristicNotification(characteristic, true);
                } catch (SecurityException e) {
                    //missing permission!
                    //TODO: notify user
                }

                // Optionally, you may need to write a descriptor to enable notifications
                //BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                //if (descriptor != null) {
                //    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                //    gatt.writeDescriptor(descriptor);
                //}
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Handle the characteristic read
                byte[] data = characteristic.getValue();
                // Process the data
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (YOUR_CHARACTERISTIC_UUID.equals(characteristic.getUuid())) {
                byte[] data = characteristic.getValue();
                String receivedData = new String(data);
                inBuffer.append(receivedData);

                //TESTING
                TextView tvC = findViewById(R.id.txtConsole);
                String temp = tvC.getText().toString();
                tvC.setText(temp + receivedData);

                //IMG switch
                ImageView iv = findViewById(R.id.ivMain);
                if(receivedData.contains("a"))
                    iv.setImageResource(R.drawable.pcm_main_0);
                else if(receivedData.contains("b"))
                    iv.setImageResource(R.drawable.pcm_0);
                else if(receivedData.contains("c"))
                    iv.setImageResource(R.drawable.pcm_1);
                else if(receivedData.contains("d"))
                    iv.setImageResource(R.drawable.pcm_2);
                else if(receivedData.contains("e"))
                    iv.setImageResource(R.drawable.pcm_3);
                else if(receivedData.contains("f"))
                    iv.setImageResource(R.drawable.pcm_4);

                List<Integer> values = DecimalParser.parseDecimals(inBuffer);

                final int LENGTH = 5;
                final int START_TOKEN = 1024;

                //contains start token?
                inBuffer_Start = -1;
                for(int i = 0; i < values.size(); i++) {
                    if(values.get(i) == START_TOKEN)
                        inBuffer_Start = i;
                }

                //full set of data
                if(inBuffer_Start >= 0 && values.size() >= inBuffer_Start + LENGTH) {
                        StringBuffer s = new StringBuffer();
                        for(int i = inBuffer_Start; i < inBuffer_Start + LENGTH; i++) {
                           s.append(values.get(i) + " ");
                        }

                        runOnUiThread(() -> {
                                TextView tv = findViewById(R.id.txtConsole);
                                tv.setText(s);
                        });

                        //setGaugeValues(values.get(0), values.get(1), values.get(2), values.get(3));

                        //clear buffer
                        inBuffer = new StringBuffer();
                        inBuffer_Start = -1;
                }
            }
        }
    };

    //**********************************************************************************************************************
    //SCAN CALLBACK
    //**********************************************************************************************************************
    private final ScanCallback leScanCallback = new ScanCallback() {

        private void dealWithResult(ScanResult result) throws SecurityException {
            if (result.getDevice().getName() != null) {
                String name = result.getDevice().getName();
                if (!names.contains(name) && !name.toLowerCase().contains("unknown")) {
                    names.add(name);
                    //recycle view
                    deviceList.add(result.getDevice());
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            TextView tv = findViewById(R.id.txtConsole);
            try {
                dealWithResult(result);
            } catch (SecurityException e) {
                //missing permission!
                //TODO: notify user
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                TextView tv = findViewById(R.id.txtConsole);
                try {
                    dealWithResult(result);
                } catch (SecurityException e) {
                    //missing permission!
                    //TODO: notify user
                }
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("MainActivity", "Scan failed with error: " + errorCode);
        }
    };

    public boolean isDeviceConnected(BluetoothDevice device) throws SecurityException {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        List<BluetoothDevice> connectedDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);

        for (BluetoothDevice connectedDevice : connectedDevices) {
            if (connectedDevice.equals(device)) {
                return true;
            }
        }
        return false;
    }

    public void disconnectDevice() throws SecurityException {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            bluetoothGatt = null;
            Snackbar.make(findViewById(R.id.loMain), "Disconnected!", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(BluetoothDevice device) {
        try {
            if(isDeviceConnected(device)) {
                //Snackbar.make(findViewById(R.id.loMain), "Device already connected!", Snackbar.LENGTH_LONG).show();
                disconnectDevice();
            }
            else{
                //connect profil
                bluetoothGatt = device.connectGatt(MainActivity.this, false, gattCallback);
                Snackbar.make(findViewById(R.id.loMain), "Trying to connect ...", Snackbar.LENGTH_LONG).show();
            }

            //bluetoothLeScanner.stopScan(leScanCallback);
        } catch (SecurityException e) {
            Snackbar.make(findViewById(R.id.loMain), "Missing permission!", Snackbar.LENGTH_LONG).show();
        }
    }
}