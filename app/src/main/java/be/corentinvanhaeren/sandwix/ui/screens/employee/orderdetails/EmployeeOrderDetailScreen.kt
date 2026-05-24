package be.corentinvanhaeren.sandwix.ui.screens.employee

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.model.BestellingDetails
import be.corentinvanhaeren.sandwix.model.BestellingItem
import be.corentinvanhaeren.sandwix.ui.components.EmployeeErrorCard
import be.corentinvanhaeren.sandwix.ui.components.EmployeeStatusPill
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.components.displayStatus
import be.corentinvanhaeren.sandwix.ui.screens.employee.orderdetails.EmployeeOrderDetailApiState
import be.corentinvanhaeren.sandwix.ui.screens.employee.orderdetails.EmployeeOrderDetailUiState
import be.corentinvanhaeren.sandwix.ui.screens.employee.orderdetails.EmployeeStatusUpdateApiState
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import java.math.BigDecimal

private val employeeStatuses = listOf(
    "Nieuw",
    "In bereiding",
    "Klaar",
    "Afgehaald",
    "Geannuleerd",
)

@Composable
internal fun EmployeeOrderDetailScreen(
    contentPadding: PaddingValues,
    uiState: EmployeeOrderDetailUiState,
    onRetry: () -> Unit,
    onStatusSave: (String) -> Unit,
) {
    val order = uiState.order

    when (uiState.apiState) {
        is EmployeeOrderDetailApiState.Loading -> {
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

        is EmployeeOrderDetailApiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(contentPadding)
                    .padding(20.dp),
                contentAlignment = Alignment.Center,
            ) {
                EmployeeErrorCard(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                )
            }
        }

        is EmployeeOrderDetailApiState.Success -> {
            if (order == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(contentPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    EmployeeErrorCard(
                        message = "Bestelling niet gevonden.",
                        onRetry = onRetry,
                    )
                }
            } else {
                EmployeeOrderDetailContent(
                    contentPadding = contentPadding,
                    order = order,
                    updateState = uiState.updateState,
                    errorMessage = uiState.errorMessage,
                    onStatusSave = onStatusSave,
                )
            }
        }
    }
}

@Composable
private fun EmployeeOrderDetailContent(
    contentPadding: PaddingValues,
    order: BestellingDetails,
    updateState: EmployeeStatusUpdateApiState,
    errorMessage: String,
    onStatusSave: (String) -> Unit,
) {
    var selectedStatus by rememberSaveable(order.bestellingId) {
        mutableStateOf(displayStatus(order.status))
    }

    val isSaving = updateState is EmployeeStatusUpdateApiState.Loading

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
                EmployeeOrderInfoCard(
                    order = order,
                    status = selectedStatus,
                )
            }

            item {
                SectionTitle("Bestelde items")
            }

            items(
                items = order.items,
                key = { item -> item.itemId },
            ) { item ->
                EmployeeOrderItemCard(item = item)
            }

            item {
                EmployeeTotalPaidCard(
                    total = order.totaalbedrag,
                )
            }

            if (updateState is EmployeeStatusUpdateApiState.Error) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            if (updateState is EmployeeStatusUpdateApiState.Success) {
                item {
                    Text(
                        text = "Status succesvol opgeslagen.",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        EmployeeStatusUpdatePanel(
            selectedStatus = selectedStatus,
            onStatusSelected = { newStatus ->
                selectedStatus = newStatus
            },
            onSave = {
                onStatusSave(selectedStatus)
            },
            isSaving = isSaving,
            modifier = Modifier.padding(
                bottom = contentPadding.calculateBottomPadding(),
            ),
        )
    }
}

@Composable
private fun EmployeeOrderInfoCard(
    order: BestellingDetails,
    status: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
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
                title = "Locatie ${order.locatieId}",
                body = "Afhaaldatum: ${order.afhaalDatum}",
            )

            EmployeeInfoRow(
                icon = Icons.Filled.AccessTime,
                title = "Afhalen om ${order.afhaalTijd}",
                body = "Code: ${order.afhaalCode ?: "-"}",
            )

            if (!order.opmerking.isNullOrBlank()) {
                Text(
                    text = "Opmerking: ${order.opmerking}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            EmployeeStatusPill(
                status = status,
            )
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
    item: BestellingItem,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "${item.hoeveelheid}x ${item.broodjeNaam}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = formatPrice(
                        BigDecimal.valueOf(item.prijsPerItem * item.hoeveelheid)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = "Basisprijs per item: ${
                    formatPrice(BigDecimal.valueOf(item.prijsPerItem))
                }",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            if (!item.opmerking.isNullOrBlank()) {
                Text(
                    text = "Opmerking: ${item.opmerking}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            val ingredienten = item.ingredienten.orEmpty()

            if (ingredienten.isNotEmpty()) {
                Text(
                    text = "Ingrediënten / extra's / zonder",
                    fontWeight = FontWeight.SemiBold,
                )

                ingredienten.forEach { ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = ingredient.naam,
                            fontWeight = FontWeight.SemiBold,
                        )

                        EmployeeSmallTag(
                            text = displayIngredientAction(ingredient.actie),
                        )

                        Spacer(Modifier.weight(1f))

                        if (ingredient.extraPrijs > 0.0) {
                            Text(
                                text = "+ ${
                                    formatPrice(BigDecimal.valueOf(ingredient.extraPrijs))
                                }",
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmployeeSmallTag(
    text: String,
) {
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
private fun EmployeeTotalPaidCard(
    total: Double,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
        ),
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
                text = formatPrice(BigDecimal.valueOf(total)),
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
    isSaving: Boolean,
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

            EmployeeStatusDropdown(
                selectedStatus = selectedStatus,
                onStatusSelected = onStatusSelected,
                modifier = Modifier.weight(1f),
            )
        }

        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text(
                text = if (isSaving) {
                    "OPSLAAN..."
                } else {
                    "STATUS OPSLAAN"
                },
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun EmployeeStatusDropdown(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = {
                expanded = true
            },
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
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            employeeStatuses.forEach { status ->
                DropdownMenuItem(
                    text = {
                        Text(status)
                    },
                    onClick = {
                        onStatusSelected(status)
                        expanded = false
                    },
                )
            }
        }
    }
}

private fun displayIngredientAction(
    actie: String,
): String {
    return when (actie.trim().lowercase()) {
        "extra" -> "extra"
        "zonder" -> "zonder"
        "remove", "removed" -> "zonder"
        "toevoegen", "add", "added" -> "extra"
        else -> actie
    }
}