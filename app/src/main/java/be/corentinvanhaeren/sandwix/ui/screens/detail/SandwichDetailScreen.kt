package be.corentinvanhaeren.sandwix.ui.screens.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.Extra
import be.corentinvanhaeren.sandwix.model.Sandwich
import be.corentinvanhaeren.sandwix.model.sampleSandwiches
import be.corentinvanhaeren.sandwix.ui.components.QuantityRow
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import be.corentinvanhaeren.sandwix.ui.util.sumOfPrice
import java.math.BigDecimal


//TODO some rendering problems
//@Preview(showBackground = true)
//@Composable
//private fun SandwichDetailScreenPreview() {
//    MaterialTheme {
//        SandwichDetailScreen(
//            sandwich = sampleSandwiches.first(),
//            onBack = {},
//            onAddToCart = {}
//        )
//    }
//}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SandwichDetailScreen(sandwich: Sandwich, onBack: () -> Unit, onAddToCart: (CartItem) -> Unit) {
    var quantity by rememberSaveable { mutableStateOf(1) }
    var note by rememberSaveable { mutableStateOf("") }
    val selectedExtras = remember { mutableStateListOf<Extra>() }
    val total = (sandwich.price + selectedExtras.sumOfPrice()).multiply(BigDecimal(quantity))

    Scaffold(topBar = { SandwixTopBar(stringResource(R.string.detail_title), onBack) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState()).padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(150.dp).clip(MaterialTheme.shapes.extraLarge).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Text(sandwich.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(sandwich.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
            SectionTitle(stringResource(R.string.ingredients_title))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                sandwich.ingredients.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
            }
            SectionTitle(stringResource(R.string.extras_title))
            sandwich.extras.forEach { extra ->
                SelectableExtraRow(extra, selected = extra in selectedExtras, onToggle = {
                    if (extra in selectedExtras) selectedExtras.remove(extra) else selectedExtras.add(extra)
                })
            }
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.note_label)) },
                placeholder = { Text(stringResource(R.string.note_placeholder)) },
                minLines = 2,
                shape = MaterialTheme.shapes.large,
            )
            QuantityRow(quantity = quantity, onChange = { quantity = it.coerceAtLeast(1) })
            Button(
                onClick = { onAddToCart(CartItem(sandwich, quantity, selectedExtras.toList(), note)) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
            ) { Text(stringResource(R.string.add_to_cart_with_price, formatPrice(total)), fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable

internal fun SelectableExtraRow(extra: Extra, selected: Boolean, onToggle: () -> Unit) {
    Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp) {
        Row(Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = selected, onCheckedChange = { onToggle() })
            Text(extra.name, Modifier.weight(1f))
            Text("+ ${formatPrice(extra.price)}", fontWeight = FontWeight.SemiBold)
        }
    }
}
