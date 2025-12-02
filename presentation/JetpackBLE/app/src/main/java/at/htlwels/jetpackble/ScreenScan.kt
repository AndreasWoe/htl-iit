package at.htlwels.jetpackble

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Preview(
    name = "Landscape Preview",
    showBackground = true,
    widthDp = 1280,  // width in dp
    heightDp = 800   // height in dp
)
@Composable
fun ScreenScanPreview() {
    val navController = rememberNavController()
    ScreenScan(navController)
}

@Composable
fun ScreenScan(navController: NavHostController, console: String = "", data: List<BluetoothDevice> = emptyList(), scan: () -> Unit = {}, connect: (BluetoothDevice) -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.pcm_main_0),
            contentDescription = null,
            contentScale = ContentScale.Fit // Adjusts how the image fits
        )
        Column {
            Row(Modifier.weight(0.1f, fill = true)) {}
            Row(Modifier.weight(weight = 0.8f, fill = true)) {
                Column(Modifier.weight(weight = 0.2f, fill = true).padding(5.0.dp))
                {
                    Text(
                        text = "System",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )
                    PCMButton(
                        text = "Start Scan",
                        0,1,
                        onClick = scan )
                    PCMButton(
                        text = "Home",
                        0,0,
                        onClick = { navController.navigate(ScreenHome) })
                }
                val scrollState = rememberScrollState()
                Column(Modifier.weight(weight = 0.6f, fill = true).fillMaxHeight()) {EntryList(Modifier, data, connect)}
                Column(Modifier.weight(weight = 0.2f, fill = true).fillMaxHeight().verticalScroll(scrollState).padding(5.0.dp)) {Text(text = console, color = Color.White)}
            }
            Row(Modifier.weight(weight = 0.1f, fill = true)) {
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
            }
        }
    }
}

@Composable
fun EntryList(modifier : Modifier = Modifier, data: List<BluetoothDevice>, connect: (BluetoothDevice) -> Unit) {
    LazyColumn (modifier = modifier) {
        items(data) { e ->
            EntryItem(e, connect)
        }
    }
}

@Composable
fun EntryItem(bd: BluetoothDevice, connect: (BluetoothDevice) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(modifier = Modifier, color = Color.White, text = bd.name ?: "Unknown Device")
        Button(onClick = { connect(bd) }) {
            Text("Connect")
        }
    }
}