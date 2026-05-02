package be.corentinvanhaeren.sandwix.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.CustomerOrder
import be.corentinvanhaeren.sandwix.model.sampleLocations
import be.corentinvanhaeren.sandwix.model.sampleOrders
import be.corentinvanhaeren.sandwix.model.sampleSandwiches
import be.corentinvanhaeren.sandwix.ui.components.MainShell
import be.corentinvanhaeren.sandwix.ui.navigation.MainTab
import be.corentinvanhaeren.sandwix.ui.navigation.Route
import be.corentinvanhaeren.sandwix.ui.screens.auth.LoginScreen
import be.corentinvanhaeren.sandwix.ui.screens.auth.RegisterScreen
import be.corentinvanhaeren.sandwix.ui.screens.cart.CartScreen
import be.corentinvanhaeren.sandwix.ui.screens.checkout.CheckoutScreen
import be.corentinvanhaeren.sandwix.ui.screens.confirmation.ConfirmationScreen
import be.corentinvanhaeren.sandwix.ui.screens.detail.SandwichDetailScreen
import be.corentinvanhaeren.sandwix.ui.screens.home.HomeScreen
import be.corentinvanhaeren.sandwix.ui.screens.orders.OrderDetailScreen
import be.corentinvanhaeren.sandwix.ui.screens.orders.OrdersScreen
import be.corentinvanhaeren.sandwix.ui.screens.profile.ProfileScreen
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme

@Composable
fun SandwixApp() {
    val backStack = remember { mutableStateListOf<Route>(Route.Login) }
    val cart = remember { mutableStateListOf<CartItem>() }
    val orders = remember { mutableStateListOf<CustomerOrder>().apply { addAll(sampleOrders()) } }
    var lastOrderId by rememberSaveable { mutableStateOf(1100) }

    fun navigate(route: Route) {
        backStack.add(route)
    }

    fun replaceWith(route: Route) {
        backStack.clear()
        backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    when (val route = backStack.last()) {
        Route.Login -> LoginScreen(
            onLogin = { replaceWith(Route.Home) },
            onRegister = { navigate(Route.Register) },
        )
        Route.Register -> RegisterScreen(
            onBack = ::goBack,
            onRegister = { replaceWith(Route.Home) },
            onLogin = ::goBack,
        )
        Route.Home -> MainShell(selectedTab = MainTab.Home, onTabSelected = ::replaceWith) { padding ->
            HomeScreen(
                contentPadding = padding,
                sandwiches = sampleSandwiches,
                onSandwichClick = { navigate(Route.Detail(it.id)) },
            )
        }
        is Route.Detail -> SandwichDetailScreen(
            sandwich = sampleSandwiches.first { it.id == route.sandwichId },
            onBack = ::goBack,
            onAddToCart = { item ->
                cart.add(item)
                replaceWith(Route.Cart)
            },
        )
        Route.Cart -> MainShell(selectedTab = MainTab.Cart, onTabSelected = ::replaceWith) { padding ->
            CartScreen(
                contentPadding = padding,
                cartItems = cart,
                onContinueShopping = { replaceWith(Route.Home) },
                onCheckout = { navigate(Route.Checkout) },
                onQuantityChange = { item, quantity ->
                    val index = cart.indexOf(item)
                    if (index != -1) {
                        if (quantity <= 0) cart.removeAt(index) else cart[index] = item.copy(quantity = quantity)
                    }
                },
                onRemove = { cart.remove(it) },
            )
        }
        Route.Checkout -> CheckoutScreen(
            cartItems = cart,
            locations = sampleLocations,
            onBack = ::goBack,
            onConfirm = { location, pickupTime, _ ->
                lastOrderId += 1
                val order = CustomerOrder(
                    id = lastOrderId,
                    pickupCode = "SWX-${lastOrderId.toString().takeLast(3)}",
                    status = "Confirmed",
                    pickupLocation = location,
                    pickupTime = pickupTime,
                    items = cart.toList(),
                )
                orders.add(0, order)
                cart.clear()
                navigate(Route.Confirmation(order.id))
            },
        )
        is Route.Confirmation -> ConfirmationScreen(
            order = orders.first { it.id == route.orderId },
            onHome = { replaceWith(Route.Home) },
            onOrders = { replaceWith(Route.Orders) },
        )
        Route.Orders -> MainShell(selectedTab = MainTab.Orders, onTabSelected = ::replaceWith) { padding ->
            OrdersScreen(
                contentPadding = padding,
                orders = orders,
                onOrderClick = { navigate(Route.OrderDetail(it.id)) },
                onHome = { replaceWith(Route.Home) },
            )
        }
        is Route.OrderDetail -> OrderDetailScreen(
            order = orders.first { it.id == route.orderId },
            onBack = ::goBack,
            onHome = { replaceWith(Route.Home) },
        )
        Route.Profile -> MainShell(selectedTab = MainTab.Profile, onTabSelected = ::replaceWith) { padding ->
            ProfileScreen(contentPadding = padding, onLogout = { replaceWith(Route.Login) })
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SandwixAppPreview() {
    SandwixTheme {
        SandwixApp()
    }
}
