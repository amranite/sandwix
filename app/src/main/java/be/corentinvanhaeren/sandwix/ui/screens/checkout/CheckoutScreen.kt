package be.corentinvanhaeren.sandwix.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.Extra
import be.corentinvanhaeren.sandwix.model.PickupLocation
import be.corentinvanhaeren.sandwix.model.sampleExtras
import be.corentinvanhaeren.sandwix.model.sampleLocations
import be.corentinvanhaeren.sandwix.model.sampleSandwiches
import be.corentinvanhaeren.sandwix.ui.components.CheckoutLine
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.components.TotalCard
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme
import be.corentinvanhaeren.sandwix.ui.util.totalPrice

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CheckoutScreen(
    contentPadding: PaddingValues,
    cartItems: List<CartItem>,
    locations: List<PickupLocation>,
    onConfirm: (PickupLocation, String, String) -> Unit,
) {
    var selectedLocationId by rememberSaveable {
        mutableStateOf(
            locations.firstOrNull { it.isOpen }?.id ?: locations.first().id
        )
    }

    var selectedTime by rememberSaveable { mutableStateOf("12:15") }
    var note by rememberSaveable { mutableStateOf("") }

    val selectedLocation = locations.first { it.id == selectedLocationId }
    val times = listOf("11:45", "12:00", "12:15", "12:30", "12:45")

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
            SectionTitle(stringResource(R.string.your_order))
        }

        items(cartItems) { cartItem ->
            CheckoutLine(cartItem)
        }

        item {
            TotalCard(cartItems.totalPrice())
        }

        item {
            SectionTitle(stringResource(R.string.choose_location))

            locations.forEach { location ->
                LocationCard(
                    location = location,
                    selected = selectedLocationId == location.id,
                    onClick = {
                        if (location.isOpen) {
                            selectedLocationId = location.id
                        }
                    },
                )

                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            SectionTitle(stringResource(R.string.choose_pickup_time))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                times.forEach { time ->
                    AssistChip(
                        onClick = {
                            selectedTime = time
                        },
                        label = {
                            Text(
                                if (time == selectedTime) {
                                    stringResource(R.string.selected_time, time)
                                } else {
                                    time
                                }
                            )
                        },
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = note,
                onValueChange = {
                    note = it
                },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.order_note_label))
                },
                placeholder = {
                    Text(stringResource(R.string.order_note_placeholder))
                },
                minLines = 3,
                shape = MaterialTheme.shapes.large,
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirm(selectedLocation, selectedTime, note)
                },
                enabled = cartItems.isNotEmpty() && selectedLocation.isOpen,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            ) {
                Text(
                    text = stringResource(R.string.confirm_and_pay),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
internal fun LocationCard(
    location: PickupLocation,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = location.isOpen,
                onClick = onClick,
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
        ),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
            )

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = location.name,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = location.address,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (!location.isOpen) {
                    Text(
                        text = stringResource(R.string.location_closed),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CheckoutScreenPreview() {
    val cartItems = listOf(
        CartItem(
            sandwich = sampleSandwiches[0],
            quantity = 2,
            selectedExtras = listOf<Extra>(sampleExtras[0]),
            note = "No onions",
        ),
        CartItem(
            sandwich = sampleSandwiches[2],
            quantity = 1,
            selectedExtras = emptyList(),
        ),
    )

    SandwixTheme (
        darkTheme = false
    ){
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.checkout_title),
                    onBack = {},
                )
            },
        ) { innerPadding ->
            CheckoutScreen(
                contentPadding = innerPadding,
                cartItems = cartItems,
                locations = sampleLocations,
                onConfirm = { _, _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CheckoutScreenPreviewDark() {
    val cartItems = listOf(
        CartItem(
            sandwich = sampleSandwiches[0],
            quantity = 2,
            selectedExtras = listOf<Extra>(sampleExtras[0]),
            note = "No onions",
        ),
        CartItem(
            sandwich = sampleSandwiches[2],
            quantity = 1,
            selectedExtras = emptyList(),
        ),
    )

    SandwixTheme (
        darkTheme = true
    ){
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.checkout_title),
                    onBack = {},
                )
            },
        ) { innerPadding ->
            CheckoutScreen(
                contentPadding = innerPadding,
                cartItems = cartItems,
                locations = sampleLocations,
                onConfirm = { _, _, _ -> },
            )
        }
    }
}