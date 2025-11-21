package at.htlwels.jetpackble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private val viewModel: BleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
                    Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                        Button(onClick = {
                            viewModel.scanLeDevice(this@MainActivity)
                        }) {
                            Text("Start Scan")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        EntryList(viewModel.devices, viewModel = viewModel)
                        Text(text = viewModel.data)
                    }
                }
            }
        }
    }
}

@Composable
fun EntryList(data: List<BluetoothDevice>, modifier : Modifier = Modifier, viewModel: BleViewModel) {
    LazyColumn (modifier = modifier) {
        items(data) { e ->
            EntryItem(e, viewModel)
            }
    }
}

@Composable
fun EntryItem(bd: BluetoothDevice, viewModel: BleViewModel) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = bd.name ?: "Unknown Device")
        Button(onClick = { viewModel.connect(bd) }) {
            Text("Connect")
        }
    }
}