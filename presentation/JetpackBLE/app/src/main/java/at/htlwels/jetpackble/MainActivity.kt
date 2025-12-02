package at.htlwels.jetpackble

import android.app.Application
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //fullscreen app - only show bars when swiped
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen(viewModel: BleViewModel = viewModel()) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ScreenHome,
    ) {
        composable<ScreenHome> {
            HomeScreen(viewModel.nav, navController, viewModel.mode, viewModel.suspension, changeMode = {viewModel.changeMode(it)}, changeSuspension = {viewModel.changeSuspension(it)})
        }
        composable<ScreenP0> {
            Parking(viewModel.nav, navController, R.drawable.pcm_0)
        }
        composable<ScreenP1> {
            Parking(viewModel.nav, navController, R.drawable.pcm_1)
        }
        composable<ScreenP2> {
            Parking(viewModel.nav, navController, R.drawable.pcm_2)
        }
        composable<ScreenP3> {
            Parking(viewModel.nav, navController, R.drawable.pcm_3)
        }
        composable<ScreenP4> {
            Parking(viewModel.nav, navController, R.drawable.pcm_4)
        }
        composable<ScreenScan> {
            ScreenScan(navController, console = viewModel.flowData.collectAsState("").value, data = viewModel.devices, scan = {viewModel.scanLeDevice()},  connect = {viewModel.connect(it) })
        }
    }
}

@Preview(
    name = "Parking Preview",
    showBackground = true,
    widthDp = 1280,  // width in dp
    heightDp = 800   // height in dp
)
@Composable
fun ParkingPreview() {
    val navController = rememberNavController()
    Parking(navController = navController, res = R.drawable.pcm_0)
}

@Composable
fun Parking(nav: SharedFlow<String> = MutableSharedFlow(), navController: NavHostController, res: Int) {
    LaunchedEffect(Unit) {
        nav.collect {  navigateTo(navController, it) }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.DarkGray)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = res),
            contentDescription = null,
            contentScale = ContentScale.Fit // Adjusts how the image fits
        )
        Column(Modifier.width(150.dp).padding(5.dp)) {
            PCMButton("Home", 0, 0, onClick = {navController.navigate(ScreenHome)})
        }
    }
}

@Preview(
    name = "Home Preview",
    showBackground = true,
    widthDp = 1280,  // width in dp
    heightDp = 800   // height in dp
)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
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
fun HomeScreen(nav: SharedFlow<String> = MutableSharedFlow(), navController: NavHostController, mode: Int = 0, suspension: Int = 0, changeMode: (Int) -> Unit = {}, changeSuspension: (Int) -> Unit = {}) {
    LaunchedEffect(Unit) {
        nav.collect { navigateTo(navController, it) }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.pcm_main_0),
            contentDescription = null,
            contentScale = ContentScale.Fit // Adjusts how the image fits
        )
        Column() {
            Row(Modifier.weight(0.05f, fill = true)) {}
            Row(Modifier.weight(weight = 0.75f, fill = true)) {
                Column(Modifier
                    .weight(weight = 0.2f, fill = true)
                    .padding(5.0.dp))
                {
                    Text(
                        text = "Modus",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )
                    PCMButton("WET", 0, mode, onClick = {changeMode(0)})
                    PCMButton("NORMAL",  1, mode, onClick = {changeMode(1)})
                    PCMButton("SPORT",  2, mode, onClick = {changeMode(2)})
                    PCMButton("SPORT PLUS",  3, mode, onClick = {changeMode(3)})
                }
                Column(Modifier.weight(weight = 0.6f, fill = true)) { }
                Column(Modifier
                    .weight(weight = 0.2f, fill = true)
                    .padding(5.0.dp)) {
                    Text(
                        text = "Fahrwerk",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )

                    PCMButton("NORMAL",  0, suspension, onClick = {changeSuspension(0)})
                    PCMButton("SPORT",  1, suspension, onClick = {changeSuspension(1)})
                    Spacer(Modifier.height(25.dp))
                    var hideSystem = false;
                    if(!hideSystem) {
                        Text(
                            text = "System",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontFamily = FontFamily.SansSerif // closest to Arial by default
                        )
                        PCMButton(
                            text = "Scan",
                            0,
                            1,
                            onClick = { navController.navigate(ScreenScan) })
                    }
                }
            }
            Row(Modifier.weight(weight = 0.2f, fill = true)) {
                Column(Modifier
                    .weight(0.25f, fill = true)
                    .fillMaxHeight()) { }
                Column(Modifier
                    .weight(0.25f, fill = true)
                    .fillMaxHeight()) { }
                Column(Modifier
                    .weight(0.25f, fill = true)
                    .fillMaxHeight()) {
                   /* Text(
                        text = "Modus",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )
                    Text(
                        text = mode.toString(),
                        color = Color.Red,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )*/
                }
                Column(Modifier
                    .weight(0.25f, fill = true)
                    .fillMaxHeight()) {
                    /*Text(
                        text = "Fahrwerk",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )
                    Text(
                        text = suspension.toString(),
                        color = Color.Red,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.SansSerif // closest to Arial by default
                    )*/
                }
            }
        }
    }
}