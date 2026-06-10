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
import be.corentinvanhaeren.sandwix.ui.components.QrCodeImage
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.components.StatusPill
import be.corentinvanhaeren.sandwix.ui.components.TotalCard
import be.corentinvanhaeren.sandwix.ui.screens.home.ErrorCard
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import java.math.BigDecimal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
                is CustomerOrdersApiState.Loading -> {
                    item {
                        LoadingBox()
                    }
                }

                is CustomerOrdersApiState.Error -> {
                    item {
                        ErrorCard(
                            message = uiState.errorMessage,
                            onRetry = onRetry,
                        )
                    }
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
        order != null -> {
            OrderDetailContent(
                contentPadding = contentPadding,
                order = order,
                onHome = onHome,
            )
        }

        uiState.apiState is CustomerOrderDetailApiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
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
                    .background(MaterialTheme.colorScheme.background)
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
    var showPickupDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val canShowPickupCode =
        order.status.equals("klaar", ignoreCase = true) &&
                order.pickupCode.isNotBlank()

    if (showPickupDialog) {
        PickupCodeDialog(
            pickupCode = order.pickupCode,
            onDismiss = {
                showPickupDialog = false
            },
        )
    }

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
            PickupCodeCard(
                pickupCode = order.pickupCode,
                canShowPickupCode = canShowPickupCode,
                onShowPickupCode = {
                    showPickupDialog = true
                },
            )
        }

        if (order.status.equals("afgehaald", ignoreCase = true)) {
            item {
                PickedUpCard()
            }
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

        items(
            items = order.items,
        ) { item ->
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
private fun PickupCodeDialog(
    pickupCode: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text("Sluiten")
            }
        },
        title = {
            Text(
                text = "Afhaalcode",
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = pickupCode,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                QrCodeImage(
                    afhaalCode = pickupCode,
                )

                Text(
                    text = "Laat deze QR-code scannen door de medewerker.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
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
                    text = stringResource(
                        R.string.pickup_code_value,
                        order.pickupCode,
                    ),
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
private fun PickupQrCard(
    pickupCode: String,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Toon deze QR-code aan de medewerker.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            QrCodeImage(
                afhaalCode = pickupCode,
            )

            Text(
                text = "De medewerker scant deze code om je bestelling te controleren.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PickedUpCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Je bestelling is afgehaald.",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            Text(
                text = "Bedankt voor je bestelling!",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                    stringResource(
                        R.string.address_value,
                        order.pickupLocation.address,
                    )
                },
            )

            InfoRow(
                icon = Icons.Filled.Info,
                text = stringResource(
                    R.string.status_value,
                    order.status,
                ),
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
private fun OrderLine(
    item: CustomerOrderLine,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.quantity_name,
                    item.quantity,
                    item.name,
                ),
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
                    text = stringResource(
                        R.string.note_value,
                        item.note,
                    ),
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
private fun PickupCodeCard(
    pickupCode: String,
    canShowPickupCode: Boolean,
    onShowPickupCode: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Afhaalcode",
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )

            if (canShowPickupCode) {
                Text(
                    text = "Je bestelling staat klaar.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                )

                Button(
                    onClick = onShowPickupCode,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = "TOON AFHAALCODE",
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else {
                Text(
                    text = "De afhaalcode wordt beschikbaar wanneer je bestelling klaar is.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
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