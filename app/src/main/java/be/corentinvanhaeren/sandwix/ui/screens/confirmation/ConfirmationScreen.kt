package be.corentinvanhaeren.sandwix.ui.screens.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.CustomerOrder
import be.corentinvanhaeren.sandwix.model.sampleOrders
import be.corentinvanhaeren.sandwix.ui.components.PickupCodeCard
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme

@Composable
internal fun ConfirmationScreen(
    contentPadding: PaddingValues,
    order: CustomerOrder,
    onHome: () -> Unit,
    onOrders: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.tertiaryContainer,
            modifier = Modifier.size(96.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.order_confirmed),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.order_success),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(24.dp))

        PickupCodeCard(order)

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onOrders,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.view_orders))
        }

        OutlinedButton(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.back_to_home))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmationScreenPreview() {
    SandwixTheme (
        darkTheme = false
    ){
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.order_confirmed),
                )
            },
        ) { innerPadding ->
            ConfirmationScreen(
                contentPadding = innerPadding,
                order = sampleOrders().first(),
                onHome = {},
                onOrders = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmationScreenPreviewDark() {
    SandwixTheme (
        darkTheme = true
    ){
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.order_confirmed),
                )
            },
        ) { innerPadding ->
            ConfirmationScreen(
                contentPadding = innerPadding,
                order = sampleOrders().first(),
                onHome = {},
                onOrders = {},
            )
        }
    }
}