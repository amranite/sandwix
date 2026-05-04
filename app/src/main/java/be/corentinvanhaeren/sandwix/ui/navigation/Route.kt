package be.corentinvanhaeren.sandwix.ui.navigation

import androidx.annotation.StringRes
import be.corentinvanhaeren.sandwix.R

internal enum class Route(
    val routeName: String,
    @StringRes val titleRes: Int,
) {
    Login(
        routeName = "login",
        titleRes = R.string.login_title,
    ),

    Register(
        routeName = "register",
        titleRes = R.string.register_title,
    ),

    Home(
        routeName = "home",
        titleRes = R.string.nav_home,
    ),

    Detail(
        routeName = "detail/{sandwichId}",
        titleRes = R.string.detail_title,
    ),

    Cart(
        routeName = "cart",
        titleRes = R.string.nav_cart,
    ),

    Checkout(
        routeName = "checkout",
        titleRes = R.string.checkout_title,
    ),

    Confirmation(
        routeName = "confirmation/{orderId}",
        titleRes = R.string.order_confirmed,
    ),

    Orders(
        routeName = "orders",
        titleRes = R.string.nav_orders,
    ),

    OrderDetail(
        routeName = "orderDetail/{orderId}",
        titleRes = R.string.nav_orders,
    ),

    Profile(
        routeName = "profile",
        titleRes = R.string.nav_profile,
    );

    companion object {
        const val SANDWICH_ID_ARG = "sandwichId"
        const val ORDER_ID_ARG = "orderId"

        fun detailRoute(sandwichId: Int): String {
            return "detail/$sandwichId"
        }

        fun confirmationRoute(orderId: Int): String {
            return "confirmation/$orderId"
        }

        fun orderDetailRoute(orderId: Int): String {
            return "orderDetail/$orderId"
        }
    }
}