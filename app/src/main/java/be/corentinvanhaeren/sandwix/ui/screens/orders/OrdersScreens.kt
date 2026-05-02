package be.corentinvanhaeren.sandwix.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CustomerOrder
import be.corentinvanhaeren.sandwix.ui.components.CheckoutLine
import be.corentinvanhaeren.sandwix.ui.components.EmptyState
import be.corentinvanhaeren.sandwix.ui.components.PickupCodeCard
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.components.StatusPill
import be.corentinvanhaeren.sandwix.ui.components.TotalCard
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import be.corentinvanhaeren.sandwix.ui.util.totalPrice

@Composable
internal fun OrdersScreen(contentPadding: PaddingValues, orders: List<CustomerOrder>, onOrderClick: (CustomerOrder) -> Unit, onHome: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, top = contentPadding.calculateTopPadding() + 24.dp, end = 20.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text(stringResource(R.string.orders_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        if (orders.isEmpty()) {
            item { EmptyState(Icons.AutoMirrored.Filled.List, stringResource(R.string.no_orders), stringResource(R.string.no_orders_body), onHome, stringResource(R.string.back_to_home)) }
        } else {
            items(orders, key = { it.id }) { order -> OrderCard(order, onClick = { onOrderClick(order) }) }
        }
    }
}

@Composable

internal fun OrderDetailScreen(order: CustomerOrder, onBack: () -> Unit, onHome: () -> Unit) {
    Scaffold(topBar = { SandwixTopBar(stringResource(R.string.order_detail_title, order.id), onBack) }) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(start = 20.dp, top = padding.calculateTopPadding() + 20.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { PickupCodeCard(order) }
            item { InfoCard(order) }
            item { SectionTitle(stringResource(R.string.ordered_items)) }
            items(order.items) { CheckoutLine(it) }
            item {
                TotalCard(order.items.totalPrice())
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.back_to_home)) }
            }
        }
    }
}

@Composable

internal fun OrderCard(order: CustomerOrder, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.order_number, order.id), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                StatusPill(order.status)
            }
            Text(stringResource(R.string.pickup_code_value, order.pickupCode), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.pickup_location_time, order.pickupLocation.name, order.pickupTime), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatPrice(order.items.totalPrice()), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
internal fun InfoCard(order: CustomerOrder) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoRow(Icons.Filled.LocationOn, stringResource(R.string.address_value, order.pickupLocation.address))
            InfoRow(Icons.Filled.Info, stringResource(R.string.status_value, order.status))
            InfoRow(Icons.Filled.Check, stringResource(R.string.selected_time, order.pickupTime))
        }
    }
}

@Composable

internal fun InfoRow(icon: ImageVector, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text)
    }
}
