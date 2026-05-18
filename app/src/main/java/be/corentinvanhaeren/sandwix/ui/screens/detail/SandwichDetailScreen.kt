package be.corentinvanhaeren.sandwix.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CartItem
import be.corentinvanhaeren.sandwix.model.Extra
import be.corentinvanhaeren.sandwix.model.Sandwich
import be.corentinvanhaeren.sandwix.ui.components.QuantityRow
import be.corentinvanhaeren.sandwix.ui.components.SectionTitle
import be.corentinvanhaeren.sandwix.ui.screens.home.ErrorCard
import be.corentinvanhaeren.sandwix.ui.util.formatPrice
import be.corentinvanhaeren.sandwix.ui.util.sumOfPrice
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.math.BigDecimal

@Composable
internal fun SandwichDetailScreen(
    contentPadding: PaddingValues,
    detailUiState: SandwichDetailUiState,
    onRetry: () -> Unit,
    onAddToCart: (CartItem) -> Unit,
) {
    val sandwich = detailUiState.sandwich

    when {
        sandwich != null -> SandwichDetailContent(
            contentPadding = contentPadding,
            sandwich = sandwich,
            onAddToCart = onAddToCart,
        )

        detailUiState.apiState is SandwichDetailApiState.Loading -> {
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
                    message = (detailUiState.apiState as? SandwichDetailApiState.Error)?.message
                        ?: detailUiState.errorMessage,
                    onRetry = onRetry,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SandwichDetailContent(
    contentPadding: PaddingValues,
    sandwich: Sandwich,
    onAddToCart: (CartItem) -> Unit,
) {
    var quantity by rememberSaveable { mutableStateOf(1) }
    var note by rememberSaveable { mutableStateOf("") }
    val selectedExtras = remember { mutableStateListOf<Extra>() }

    val total = (sandwich.price + selectedExtras.sumOfPrice())
        .multiply(BigDecimal(quantity))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            SandwichHeroImage(sandwich = sandwich)
        }

        Text(
            text = sandwich.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = sandwich.description,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Text(
            text = formatPrice(sandwich.price),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        SectionTitle(stringResource(R.string.ingredients_title))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            sandwich.ingredients.forEach { ingredient ->
                AssistChip(
                    onClick = {},
                    label = {
                        Text(ingredient)
                    },
                )
            }
        }

        SectionTitle(stringResource(R.string.extras_title))

        sandwich.extras.forEach { extra ->
            SelectableExtraRow(
                extra = extra,
                selected = extra in selectedExtras,
                onToggle = {
                    if (extra in selectedExtras) {
                        selectedExtras.remove(extra)
                    } else {
                        selectedExtras.add(extra)
                    }
                },
            )
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(stringResource(R.string.note_label))
            },
            placeholder = {
                Text(stringResource(R.string.note_placeholder))
            },
            minLines = 2,
            shape = MaterialTheme.shapes.large,
        )

        QuantityRow(
            quantity = quantity,
            onChange = { quantity = it.coerceAtLeast(1) },
        )

        Button(
            onClick = {
                onAddToCart(
                    CartItem(
                        sandwich = sandwich,
                        quantity = quantity,
                        selectedExtras = selectedExtras.toList(),
                        note = note,
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
        ) {
            Text(
                text = stringResource(
                    R.string.add_to_cart_with_price,
                    formatPrice(total),
                ),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SandwichHeroImage(
    sandwich: Sandwich,
) {
    if (sandwich.imageUrl.isBlank()) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        return
    }

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(sandwich.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Foto van ${sandwich.name}",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
internal fun SelectableExtraRow(
    extra: Extra,
    selected: Boolean,
    onToggle: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = selected,
                onCheckedChange = {
                    onToggle()
                },
            )

            Text(
                text = extra.name,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = "+ ${formatPrice(extra.price)}",
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}
