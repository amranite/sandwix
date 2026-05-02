package be.corentinvanhaeren.sandwix.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import be.corentinvanhaeren.sandwix.R

internal enum class MainTab(val route: Route, val labelRes: Int, val icon: ImageVector) {
    Home(Route.Home, R.string.nav_home, Icons.Filled.Home),
    Orders(Route.Orders, R.string.nav_orders, Icons.AutoMirrored.Filled.List),
    Cart(Route.Cart, R.string.nav_cart, Icons.Filled.ShoppingCart),
    Profile(Route.Profile, R.string.nav_profile, Icons.Filled.Person),
}

