package be.corentinvanhaeren.sandwix.ui.screens.orders

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.ui.components.EmptyState
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.components.StatusPill
import be.corentinvanhaeren.sandwix.ui.components.TotalCard
import be.corentinvanhaeren.sandwix.ui.screens.home.ErrorCard
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun OrdersScreen(
    contentPadding: PaddingValues,
    uiState: CustomerOrdersUiState,
    onOrderClick: (CustomerOrderSummary) -> Unit,
    onHome: () -> Unit,
    onRetry: () -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = uiState.apiState is CustomerOrdersApiState.Loading,
        onRefresh = onRetry,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                    text = stringResource(R.string.orders_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            when (uiState.apiState) {
                is CustomerOrdersApiState.Loading -> item {
                    LoadingBox()
                }

                is CustomerOrdersApiState.Error -> item {
                    ErrorCard(
                        message = uiState.errorMessage,
                        onRetry = onRetry,
                    )
                }

                is CustomerOrdersApiState.Success -> {
                    if (uiState.orders.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.AutoMirrored.Filled.List,
                                title = stringResource(R.string.no_orders),
                                body = stringResource(R.string.no_orders_body),
                                onAction = onHome,
                                actionLabel = stringResource(R.string.back_to_home),
                            )
                        }
                    } else {
                        items(
                            items = uiState.orders,
                            key = { order -> order.id },
                        ) { order ->
                            OrderCard(
                                order = order,
                                onClick = {
                                    onOrderClick(order)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun OrderDetailScreen(
    contentPadding: PaddingValues,
    uiState: CustomerOrderDetailUiState,
    onRetry: () -> Unit,
    onHome: () -> Unit,
) {
    val order = uiState.order

    when {
        order != null -> OrderDetailContent(
            contentPadding = contentPadding,
            order = order,
            onHome = onHome,
        )

        uiState.apiState is CustomerOrderDetailApiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                ErrorCard(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                )
            }
        }
    }
}

@Composable
private fun OrderDetailContent(
    contentPadding: PaddingValues,
    order: CustomerOrderDetail,
    onHome: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 20.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 24.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            PickupCodeCard(order.pickupCode)
        }

        item {
            InfoCard(order)
        }

        if (!order.note.isNullOrBlank()) {
            item {
                Text(
                    text = stringResource(R.string.note_value, order.note),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            SectionTitle(stringResource(R.string.ordered_items))
        }

        items(order.items) { item ->
            OrderLine(item)
        }

        item {
            TotalCard(order.total)

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onHome,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.back_to_home))
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: CustomerOrderSummary,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.order_number, order.id),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                StatusPill(order.status)
            }

            if (order.pickupCode.isNotBlank()) {
                Text(
                    text = stringResource(R.string.pickup_code_value, order.pickupCode),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = stringResource(
                    R.string.pickup_location_date_time,
                    order.pickupLocation.name,
                    order.pickupDate,
                    order.pickupTime,
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = formatPrice(order.total),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun InfoCard(
    order: CustomerOrderDetail,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            InfoRow(
                icon = Icons.Filled.LocationOn,
                text = if (order.pickupLocation.address.isBlank()) {
                    order.pickupLocation.name
                } else {
                    stringResource(R.string.address_value, order.pickupLocation.address)
                },
            )

            InfoRow(
                icon = Icons.Filled.Info,
                text = stringResource(R.string.status_value, order.status),
            )

            InfoRow(
                icon = Icons.Filled.Check,
                text = stringResource(
                    R.string.pickup_date_time,
                    order.pickupDate,
                    order.pickupTime,
                ),
            )
        }
    }
}

@Composable
private fun OrderLine(item: CustomerOrderLine) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = stringResource(R.string.quantity_name, item.quantity, item.name),
                fontWeight = FontWeight.SemiBold,
            )

            item.ingredients.forEach { ingredient ->
                Text(
                    text = stringResource(
                        R.string.ingredient_change,
                        ingredient.action,
                        ingredient.name,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (!item.note.isNullOrBlank()) {
                Text(
                    text = stringResource(R.string.note_value, item.note),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Text(
            text = formatPrice(item.lineTotal()),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun PickupCodeCard(pickupCode: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.pickup_code),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Text(
                text = pickupCode,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Text(
                text = stringResource(R.string.show_code_at_pickup),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    text: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(text)
    }
}

@Composable
private fun LoadingBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

private fun CustomerOrderLine.lineTotal(): BigDecimal {
    val extras = ingredients.fold(BigDecimal.ZERO) { total, ingredient ->
        total + ingredient.extraPrice
    }
    return (unitPrice + extras).multiply(BigDecimal(quantity))
}
