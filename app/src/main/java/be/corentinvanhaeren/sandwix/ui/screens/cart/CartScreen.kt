package be.corentinvanhaeren.sandwix.ui.screens.cart

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.Extra
import be.corentinvanhaeren.sandwix.model.sampleExtras
import be.corentinvanhaeren.sandwix.model.sampleSandwiches
import be.corentinvanhaeren.sandwix.ui.components.EmptyState
import be.corentinvanhaeren.sandwix.ui.components.QuantityRow
import be.corentinvanhaeren.sandwix.ui.components.TotalCard
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import be.corentinvanhaeren.sandwix.ui.util.lineTotal
import be.corentinvanhaeren.sandwix.ui.util.totalPrice

@Preview(showBackground = true)
@Composable
private fun CartScreenPreview() {

    val cartItems = listOf(
        CartItem(
            sandwich = sampleSandwiches[0],
            quantity = 2,
            selectedExtras = listOf<Extra>(
                sampleExtras[0],
                sampleExtras[1]
            ),
            note = "No onions"
        ),
        CartItem(
            sandwich = sampleSandwiches[2],
            quantity = 1
        )
    )

    CartScreen(
        contentPadding = PaddingValues(0.dp),
        cartItems = cartItems,
        onContinueShopping = {},
        onCheckout = {},
        onQuantityChange = { _, _ -> },
        onRemove = {}
    )
}

@Composable
internal fun CartScreen(
    contentPadding: PaddingValues,
    cartItems: List<CartItem>,
    onContinueShopping: () -> Unit,
    onCheckout: () -> Unit,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemove: (CartItem) -> Unit,
) {
    val total = cartItems.totalPrice()
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(start = 20.dp, top = contentPadding.calculateTopPadding() + 24.dp, end = 20.dp, bottom = contentPadding.calculateBottomPadding() + 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text(stringResource(R.string.cart_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        if (cartItems.isEmpty()) {
            item { EmptyState(Icons.Filled.ShoppingCart, stringResource(R.string.empty_cart), stringResource(R.string.empty_cart_body), onContinueShopping, stringResource(R.string.continue_shopping)) }
        } else {
            items(cartItems) { item -> CartItemCard(item, onQuantityChange, onRemove) }
            item {
                TotalCard(total)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onCheckout, modifier = Modifier.fillMaxWidth().height(54.dp)) { Text(stringResource(R.string.checkout_action), fontWeight = FontWeight.Bold) }
                OutlinedButton(onClick = onContinueShopping, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.continue_shopping)) }
            }
        }
    }
}

@Composable

internal fun CartItemCard(item: CartItem, onQuantityChange: (CartItem, Int) -> Unit, onRemove: (CartItem) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(item.sandwich.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (item.selectedExtras.isNotEmpty()) Text(item.selectedExtras.joinToString { it.name }, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (item.note.isNotBlank()) Text(stringResource(R.string.note_value, item.note), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = { onRemove(item) }) { Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.cd_remove)) }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityRow(item.quantity) { onQuantityChange(item, it) }
                Spacer(Modifier.weight(1f))
                Text(formatPrice(item.lineTotal()), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}
