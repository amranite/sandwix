package be.corentinvanhaeren.sandwix.ui.screens.employee

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.ui.components.SandwixEmployeeBottomBar
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.navigation.EmployeeTab
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme

@Composable
internal fun EmployeeProfileScreen(
    contentPadding: PaddingValues,
    name: String,
    email: String,
    onPersonalDataClick: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 6.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "M",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = email,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column {
                EmployeeProfileActionRow(
                    icon = Icons.Filled.PersonOutline,
                    text = "Persoonlijke gegevens",
                    onClick = onPersonalDataClick,
                )

                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )

                EmployeeProfileActionRow(
                    icon = Icons.Filled.Logout,
                    text = "Uitloggen",
                    onClick = onLogout,
                    isDestructive = true,
                )
            }
        }
    }
}

@Composable
private fun EmployeeProfileActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val contentColor = if (isDestructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
        )
        Text(
            text = text,
            color = contentColor,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EmployeeProfileScreenPreview() {
    SandwixTheme(darkTheme = false) {
        Scaffold(
            topBar = {
                SandwixTopBar(title = "Mijn Account")
            },
            bottomBar = {
                SandwixEmployeeBottomBar(
                    selectedTab = EmployeeTab.Profile,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            EmployeeProfileScreen(
                contentPadding = innerPadding,
                name = "medewerker",
                email = "medewerker@gmail.com",
                onPersonalDataClick = {},
                onLogout = {},
            )
        }
    }
}
