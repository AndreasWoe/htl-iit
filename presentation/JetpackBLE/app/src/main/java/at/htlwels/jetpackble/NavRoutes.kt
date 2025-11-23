package at.htlwels.jetpackble

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Parking_0 : NavRoutes("parking_0")
}