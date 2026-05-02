package be.corentinvanhaeren.sandwix.ui.navigation

internal sealed interface Route {
    data object Login : Route
    data object Register : Route
    data object Home : Route
    data class Detail(val sandwichId: Int) : Route
    data object Cart : Route
    data object Checkout : Route
    data class Confirmation(val orderId: Int) : Route
    data object Orders : Route
    data class OrderDetail(val orderId: Int) : Route
    data object Profile : Route
}

