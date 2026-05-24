package be.corentinvanhaeren.sandwix.ui.screens.employee.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.model.BestellingOverzicht
import be.corentinvanhaeren.sandwix.ui.components.EmployeeErrorCard
import be.corentinvanhaeren.sandwix.ui.components.EmployeeStatusPill
import be.corentinvanhaeren.sandwix.ui.components.EmptyState
import be.corentinvanhaeren.sandwix.ui.components.displayStatus
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EmployeeOrdersScreen(
    contentPadding: PaddingValues,
    uiState: EmployeeOrdersUiState,
    onRetry: () -> Unit,
    onOrderClick: (BestellingOverzicht) -> Unit,
) {
    val isRefreshing = uiState.apiState is EmployeeOrdersApiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
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
                    text = "Openstaande bestellingen",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            when (uiState.apiState) {
                is EmployeeOrdersApiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is EmployeeOrdersApiState.Error -> {
                    item {
                        EmployeeErrorCard(
                            message = uiState.errorMessage,
                            onRetry = onRetry,
                        )
                    }
                }

                is EmployeeOrdersApiState.Success -> {
                    if (uiState.orders.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.AutoMirrored.Filled.List,
                                title = "Geen bestellingen",
                                body = "Er zijn vandaag nog geen bestellingen.",
                                onAction = onRetry,
                                actionLabel = "Vernieuwen",
                            )
                        }
                    } else {
                        items(
                            items = uiState.orders,
                            key = { order -> order.bestellingId },
                        ) { order ->
                            EmployeeOrderCard(
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
private fun EmployeeOrderCard(
    order: BestellingOverzicht,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = "Bestelling #${order.bestellingId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = "Afhalen om ${order.afhaalTijd}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = formatPriceFromString(order.totaalbedrag),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = "Code: ${order.afhaalCode ?: "-"}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            EmployeeStatusPill(
                status = displayStatus(order.status),
            )
        }
    }
}

private fun formatPriceFromString(
    amount: String,
): String {
    val value = amount
        .replace(",", ".")
        .toDoubleOrNull()

    return if (value != null) {
        formatPrice(BigDecimal.valueOf(value))
    } else {
        "€ $amount"
    }
}