package be.corentinvanhaeren.sandwix.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector
import be.corentinvanhaeren.sandwix.R

internal enum class EmployeeTab(
    val route: Route,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Orders(
        route = Route.EmployeeOrders,
        labelRes = R.string.employee_orders_title,
        icon = Icons.AutoMirrored.Filled.List,
    ),
    Scan(
        route = Route.EmployeeScan,
        labelRes = R.string.employee_scan_title,
        icon = Icons.Filled.QrCodeScanner,
    ),
    Profile(
        route = Route.EmployeeProfile,
        labelRes = R.string.employee_profile_title,
        icon = Icons.Filled.Person,
    ),
}
