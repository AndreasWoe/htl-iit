package at.htlwels.jetpackble

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val viewModel: BleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.initBLE()

        setContent {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                MainScreen(viewModel)
            }
        }
    }
}

@Preview(
name = "Landscape Preview",
showBackground = true,
widthDp = 1280,  // width in dp
heightDp = 800   // height in dp
)
@Composable
fun MainScreenPreview() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val viewModel: BleViewModel = BleViewModel(Application())
        MainScreen(viewModel)
    }
}

@Composable
fun PCMButton(text: String, enabled: Boolean = false, onClick: () -> Unit = {}) {
    var c: Color
    var lc: Color = Color.Transparent

    if(enabled) {
        c = Color(0xFF989A9B)
        lc = Color.Red
    } else {
        c = Color(0xFF313234)
        lc = c
    }

    Column(Modifier.padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Button(
            onClick = { onClick() },
            modifier = Modifier.fillMaxWidth(1.0f).defaultMinSize(minHeight = 50.dp),
            shape = RectangleShape, // makes it rectangular
            colors = ButtonDefaults.buttonColors(containerColor = c)
        )
        {
            Text(modifier = Modifier,
                text = text,
                color = Color.White,
                //fontSize = 28.sp,
                fontFamily = FontFamily.SansSerif // closest to Arial by default
            )
        }
        Box(
            modifier = Modifier
                .height(4.dp)
                .fillMaxWidth()
                .background(lc)
        )
    }
}

@Composable
fun MainScreen(viewModel: BleViewModel) {

    var mode_wet by remember { mutableStateOf(false) }
    var mode_normal by remember { mutableStateOf(true) }
    var mode_sport by remember { mutableStateOf(false) }
    var mode_sport_plus by remember { mutableStateOf(false) }

    val suspension = remember { mutableStateListOf(true, false) }

    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(id = R.drawable.pcm_main_0),
        contentDescription = null,
        contentScale = ContentScale.Fit // Adjusts how the image fits
    )
Column() {
    Row(Modifier.weight(0.05f, fill=true)) {}
    Row(Modifier.weight(weight = 0.75f, fill = true)) {
        Column(Modifier.weight(weight = 0.2f, fill = true).padding(5.0.dp))
        {
            val on_mode_wet = {mode_wet=true; mode_normal=false; mode_sport=false; mode_sport_plus=false}
            val on_mode_normal = {mode_normal=true; mode_wet=false; mode_sport=false; mode_sport_plus=false}
            val on_mode_sport = {mode_sport=true; mode_wet=false; mode_normal=false; mode_sport_plus=false}
            val on_mode_sport_plus = {mode_sport_plus=true; mode_wet=false; mode_normal=false; mode_sport=false}

            Text(
                text = "Modus",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily.SansSerif // closest to Arial by default
            )
            PCMButton("WET", enabled = mode_wet, onClick = on_mode_wet)
            PCMButton("NORMAL", enabled = mode_normal, onClick = on_mode_normal)
            PCMButton("SPORT", enabled = mode_sport, onClick = on_mode_sport)
            PCMButton("SPORT PLUS", enabled = mode_sport_plus, onClick = on_mode_sport_plus)
        }
        Column(Modifier.weight(weight = 0.6f, fill = true)) { }
        Column(Modifier.weight(weight = 0.2f, fill = true).padding(5.0.dp)) {
            Text(
                text = "Fahrwerk",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily.SansSerif // closest to Arial by default
            )

            val on_suspension_normal = {suspension[0]=true; suspension[1]=false}
            val on_suspension_sport = {suspension[0]=false; suspension[1]=true}

            PCMButton("NORMAL", enabled = suspension[0], onClick = on_suspension_normal)
            PCMButton("SPORT", enabled = suspension[1], onClick = on_suspension_sport)
            Spacer(Modifier.height(25.dp))
            Text(
                text = "System",
                color = Color.White,
                fontSize = 25.sp,
                fontFamily = FontFamily.SansSerif // closest to Arial by default
            )

            PCMButton(text="Scan", enabled = false, onClick = { viewModel.scanLeDevice() })
        }
    }
    Row(Modifier.weight(weight = 0.2f, fill = true)) {
        val scrollState = rememberScrollState()

        Column(Modifier.weight(0.25f, fill=true).fillMaxHeight()) { }
        Column(Modifier.weight(0.25f, fill=true).fillMaxHeight()) { }
        Column(Modifier.weight(0.25f, fill=true).fillMaxHeight()) {EntryList(viewModel.devices, viewModel = viewModel) }
        Column(Modifier.weight(0.25f, fill=true).fillMaxHeight().verticalScroll(scrollState)) {Text(text = viewModel.data, color = Color.White) }
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