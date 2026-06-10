package be.corentinvanhaeren.sandwix.ui.screens.employee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.CustomerOrder
import be.corentinvanhaeren.sandwix.model.sampleOrders
import be.corentinvanhaeren.sandwix.ui.components.EmptyState
import be.corentinvanhaeren.sandwix.ui.components.SandwixEmployeeBottomBar
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.navigation.EmployeeTab
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import be.corentinvanhaeren.sandwix.ui.util.lineTotal
import be.corentinvanhaeren.sandwix.ui.util.totalPrice
import java.math.BigDecimal

private val employeeStatuses = listOf(
    "Nieuw",
    "In bereiding",
    "Klaar",
    //"Afgehaald",
    //"Geannuleerd",
)

@Composable
internal fun EmployeeOrdersScreen(
    contentPadding: PaddingValues,
    orders: List<CustomerOrder>,
    onOrderClick: (CustomerOrder) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 24.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text(
                text = "Openstaande bestellingen",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )

            Text(
                text = "Volg, bekijk en werk bestellingen bij.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (orders.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = "Geen bestellingen",
                    body = "Er zijn momenteel geen openstaande bestellingen.",
                    onAction = {},
                    actionLabel = "Vernieuwen",
                )
            }
        } else {
            items(
                items = orders,
                key = { order -> order.id },
            ) { order ->
                EmployeeOrderCard(
                    order = order,
                    onClick = { onOrderClick(order) },
                )
            }
        }
    }
}

@Composable
internal fun EmployeeOrderDetailScreen(
    contentPadding: PaddingValues,
    order: CustomerOrder,
    onStatusSave: (CustomerOrder, String) -> Unit,
) {
    var selectedStatus by rememberSaveable(order.id) {
        mutableStateOf(displayStatus(order.status))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = contentPadding.calculateTopPadding()),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 20.dp,
                end = 20.dp,
                bottom = 20.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                EmployeeOrderInfoCard(order = order, status = selectedStatus)
            }

            item {
                SectionTitle("Bestelde items")
            }

            items(order.items) { item ->
                EmployeeOrderItemCard(item = item)
            }

            item {
                EmployeeTotalPaidCard(total = order.items.totalPrice())
            }
        }

        EmployeeStatusUpdatePanel(
            selectedStatus = selectedStatus,
            onStatusSelected = { selectedStatus = it },
            onSave = { onStatusSave(order, selectedStatus) },
            modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()),
        )
    }
}

@Composable
private fun EmployeeOrderCard(
    order: CustomerOrder,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.order_number, order.id),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Afhalen om ${order.pickupTime}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = formatPrice(order.items.totalPrice()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = "Code: ${order.pickupCode}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            EmployeeStatusPill(status = displayStatus(order.status))
        }
    }
}

@Composable
private fun EmployeeOrderInfoCard(
    order: CustomerOrder,
    status: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Bestelling informatie",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            EmployeeInfoRow(
                icon = Icons.Filled.LocationOn,
                title = order.pickupLocation.name,
                body = order.pickupLocation.address,
            )

            EmployeeInfoRow(
                icon = Icons.Filled.AccessTime,
                title = "Afhalen om ${order.pickupTime}",
                body = "Code: ${order.pickupCode}",
            )

            EmployeeStatusPill(status = status)
        }
    }
}

@Composable
private fun EmployeeInfoRow(
    icon: ImageVector,
    title: String,
    body: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Column {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = body,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun EmployeeOrderItemCard(
    item: CartItem,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${item.quantity}x ${item.sandwich.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = formatPrice(item.lineTotal()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "Basisprijs per item: ${formatPrice(item.sandwich.price)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (item.selectedExtras.isNotEmpty()) {
                Text(
                    text = "Ingrediënten / extra's / zonder",
                    fontWeight = FontWeight.SemiBold,
                )

                item.selectedExtras.forEach { extra ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = extra.name,
                            fontWeight = FontWeight.SemiBold,
                        )
                        EmployeeSmallTag(text = "extra")
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "+ ${formatPrice(extra.price)}",
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            if (item.note.isNotBlank()) {
                Text(
                    text = stringResource(R.string.note_value, item.note),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun EmployeeSmallTag(text: String) {
    Surface(
        modifier = Modifier.padding(start = 8.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun EmployeeTotalPaidCard(total: BigDecimal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Totaal betaald",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = formatPrice(total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun EmployeeStatusUpdatePanel(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Status",
                fontWeight = FontWeight.SemiBold,
            )

            StatusDropdown(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected,
                modifier = Modifier.weight(1f),
            )
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(
                text = "STATUS OPSLAAN",
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun StatusDropdown(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = selectedStatus,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
        ) {
            employeeStatuses.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status) },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun EmployeeStatusPill(status: String) {
    val (containerColor, contentColor) = employeeStatusColors(status)

    Surface(
        shape = CircleShape,
        color = containerColor,
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
        )
    }
}

@Composable
private fun employeeStatusColors(status: String): Pair<Color, Color> {
    return when (status.lowercase()) {
        "klaar" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "afgehaald" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        "geannuleerd" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "nieuw" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) to MaterialTheme.colorScheme.primary
    }
}

private fun displayStatus(status: String): String {
    return when (status.trim().lowercase()) {
        "new", "nieuw" -> "Nieuw"
        "in_preparation", "preparing", "in_bereiding", "in bereiding" -> "In bereiding"
        "ready", "klaar" -> "Klaar"
        "completed", "picked_up", "afgehaald" -> "Afgehaald"
        "cancelled", "canceled", "geannuleerd" -> "Geannuleerd"
        else -> status.ifBlank { "Nieuw" }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EmployeeOrdersScreenPreview() {
    SandwixTheme(darkTheme = false) {
        Scaffold(
            topBar = {
                SandwixTopBar(title = stringResource(R.string.employee_orders_title))
            },
            bottomBar = {
                SandwixEmployeeBottomBar(
                    selectedTab = EmployeeTab.Orders,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            EmployeeOrdersScreen(
                contentPadding = innerPadding,
                orders = sampleOrders(),
                onOrderClick = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EmployeeOrderDetailScreenPreview() {
    val order = sampleOrders().first()

    SandwixTheme(darkTheme = false) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.order_detail_title, order.id),
                    onBack = {},
                )
            },
            bottomBar = {
                SandwixEmployeeBottomBar(
                    selectedTab = EmployeeTab.Orders,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            EmployeeOrderDetailScreen(
                contentPadding = innerPadding,
                order = order,
                onStatusSave = { _, _ -> },
            )
        }
    }
}
