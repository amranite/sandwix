package be.corentinvanhaeren.sandwix.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import be.corentinvanhaeren.sandwix.ui.components.SandwixBottomBar
import be.corentinvanhaeren.sandwix.ui.components.SandwixTopBar
import be.corentinvanhaeren.sandwix.ui.navigation.MainTab
import be.corentinvanhaeren.sandwix.ui.theme.SandwixTheme

@Composable
internal fun ProfileScreen(
    contentPadding: PaddingValues,
    onLogout: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary,
        )

        Text(
            text = stringResource(R.string.profile_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = stringResource(R.string.profile_placeholder),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.logout))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    SandwixTheme(
        darkTheme = false
    ) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.nav_profile),
                )
            },
            bottomBar = {
                SandwixBottomBar(
                    selectedTab = MainTab.Profile,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            ProfileScreen(
                contentPadding = innerPadding,
                onLogout = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreviewDark() {
    SandwixTheme(
        darkTheme = true
    ) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.nav_profile),
                )
            },
            bottomBar = {
                SandwixBottomBar(
                    selectedTab = MainTab.Profile,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            ProfileScreen(
                contentPadding = innerPadding,
                onLogout = {},
            )
        }
    }
}