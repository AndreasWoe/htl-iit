package at.htlwels.jetpackble

import android.app.Application
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {

    private val viewModel: BleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fullscreen app - only show bars when swiped
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        viewModel.initBLE()
        setContent {
            MainScreen(viewModel)
        }
    }
}

@Composable
fun MainScreen(viewModel: BleViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenHome,
    ) {
        composable<ScreenHome> {
            HomeScreen(viewModel, navController)
        }
        composable<ScreenP0> {
            Parking(viewModel, navController, R.drawable.pcm_0)
        }
        composable<ScreenP1> {
            Parking(viewModel, navController, R.drawable.pcm_1)
        }
        composable<ScreenP2> {
            Parking(viewModel, navController, R.drawable.pcm_2)
        }
        composable<ScreenP3> {
            Parking(viewModel, navController, R.drawable.pcm_3)
        }
        composable<ScreenP4> {
            Parking(viewModel, navController, R.drawable.pcm_4)
        }
        composable<ScreenScan> {
            ScreenScan(viewModel, navController)
        }
    }
}

@Composable
fun Parking(viewModel: BleViewModel = viewModel(), navController: NavHostController, res: Int) {
    LaunchedEffect(Unit) {
        viewModel.nav.collect { event -> navigateTo(navController, event) }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = res),
            contentDescription = null,
            contentScale = ContentScale.Fit // Adjusts how the image fits
        )
    }
}

@Preview(
    name = "Landscape Preview",
    showBackground = true,
    widthDp = 1280,  // width in dp
    heightDp = 800   // height in dp
)
@Composable
fun HomeScreenPreview(viewModel: BleViewModel = BleViewModel(Application())) {
    val navController = rememberNavController()
    HomeScreen(viewModel, navController)
}

private fun navigateTo(navController: NavHostController, destination: String) {
    when (destination) {
        "home" -> navController.navigate(ScreenHome)
        "p_0" -> navController.navigate(ScreenP0)
        "p_1" -> navController.navigate(ScreenP1)
        "p_2" -> navController.navigate(ScreenP2)
        "p_3" -> navController.navigate(ScreenP3)
        "p_4" -> navController.navigate(ScreenP4)
    }
}

@Composable
fun HomeScreen(viewModel: BleViewModel = viewModel(), navController: NavHostController) {
    LaunchedEffect(Unit) {
        viewModel.nav.collect { event -> navigateTo(navController, event) }
    }

    var mode_wet by remember { mutableStateOf(false) }
    var mode_normal by remember { mutableStateOf(true) }
    var mode_sport by remember { mutableStateOf(false) }
    var mode_sport_plus by remember { mutableStateOf(false) }

    val suspension = remember { mutableStateListOf(true, false) }
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.pcm_main_0),
            contentDescription = null,
            contentScale = ContentScale.Fit // Adjusts how the image fits
        )
        Column() {
            Row(Modifier.weight(0.05f, fill = true)) {}
            Row(Modifier.weight(weight = 0.75f, fill = true)) {
                Column(Modifier.weight(weight = 0.2f, fill = true).padding(5.0.dp))
                {
                    val on_mode_wet = {
                        mode_wet = true; mode_normal = false; mode_sport = false; mode_sport_plus =
                        false
                    }
                    val on_mode_normal = {
                        mode_normal = true; mode_wet = false; mode_sport = false; mode_sport_plus =
                        false
                    }
                    val on_mode_sport = {
                        mode_sport = true; mode_wet = false; mode_normal = false; mode_sport_plus =
                        false
                    }
                    val on_mode_sport_plus = {
                        mode_sport_plus = true; mode_wet = false; mode_normal = false; mode_sport =
                        false
                    }

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

                    val on_suspension_normal = { suspension[0] = true; suspension[1] = false }
                    val on_suspension_sport = { suspension[0] = false; suspension[1] = true }

                    PCMButton("NORMAL", enabled = suspension[0], onClick = on_suspension_normal)
                    PCMButton("SPORT", enabled = suspension[1], onClick = on_suspension_sport)
                    Spacer(Modifier.height(25.dp))
                    Text(
                        text = "System",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )

                    PCMButton(
                        text = "Scan",
                        enabled = false,
                        onClick = { navController.navigate(ScreenScan) })
                }
            }
            Row(Modifier.weight(weight = 0.2f, fill = true)) {
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
                Column(Modifier.weight(0.25f, fill = true).fillMaxHeight()) { }
            }
        }
    }
}