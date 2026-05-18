package be.corentinvanhaeren.sandwix.ui.screens.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.corentinvanhaeren.sandwix.R
import be.corentinvanhaeren.sandwix.model.Sandwich
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    contentPadding: PaddingValues,
    homeUiState: HomeUiState,
    onQueryUpdate: (String) -> Unit,
    onRetry: () -> Unit,
    onSandwichClick: (Sandwich) -> Unit,
) {
    val isRefreshing = homeUiState.apiState is HomeApiState.Loading

    val filtered = homeUiState.sandwiches.filter { sandwich ->
        sandwich.name.contains(homeUiState.query, ignoreCase = true)
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRetry,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    text = stringResource(R.string.home_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = stringResource(R.string.home_subtitle),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = homeUiState.query,
                    onValueChange = onQueryUpdate,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(stringResource(R.string.search_placeholder))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                        )
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                )

                Spacer(Modifier.height(12.dp))

                DeliveryNotice()
            }

            when (homeUiState.apiState) {
                is HomeApiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is HomeApiState.Error -> {
                    item {
                        ErrorCard(
                            message = (homeUiState.apiState as? HomeApiState.Error)?.message
                                ?: homeUiState.errorMessage,
                            onRetry = onRetry
                        )
                    }
                }

                is HomeApiState.Success -> {
                    items(
                        items = filtered,
                        key = { sandwich -> sandwich.id },
                    ) { sandwich ->
                        SandwichCard(
                            sandwich = sandwich,
                            onClick = {
                                onSandwichClick(sandwich)
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onErrorContainer,
                fontWeight = FontWeight.Bold,
            )

            Button(onClick = onRetry) {
                Text("Opnieuw proberen")
            }
        }
    }
}

@Composable
internal fun DeliveryNotice() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )

            Column {
                Text(
                    text = stringResource(R.string.company_delivery_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )

                Text(
                    text = stringResource(R.string.company_delivery_body),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
            }
        }
    }
}

@Composable
internal fun SandwichCard(
    sandwich: Sandwich,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SandwichImage(sandwich = sandwich)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = sandwich.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Vers belegd broodje",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "€ ${sandwich.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
@Composable
internal fun SandwichImage(
    sandwich: Sandwich,
    modifier: Modifier = Modifier,
) {
    if (sandwich.imageUrl.isBlank()) {
        SandwichIcon(modifier = modifier)
        return
    }

    AsyncImage(
        model = ImageRequest.Builder(context = LocalContext.current)
            .data(sandwich.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Foto van ${sandwich.name}",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(88.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant),
    )
}
@Composable
internal fun SandwichIcon(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(88.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.MenuBook,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    SandwixTheme(
        darkTheme = false,
    ) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.nav_home),
                )
            },
            bottomBar = {
                SandwixBottomBar(
                    selectedTab = MainTab.Home,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            HomeScreen(
                contentPadding = innerPadding,
                sandwiches = sampleSandwiches,
                onSandwichClick = {},
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreviewDark() {
    SandwixTheme(
        darkTheme = true,
    ) {
        Scaffold(
            topBar = {
                SandwixTopBar(
                    title = stringResource(R.string.nav_home),
                )
            },
            bottomBar = {
                SandwixBottomBar(
                    selectedTab = MainTab.Home,
                    onTabSelected = {},
                )
            },
        ) { innerPadding ->
            HomeScreen(
                contentPadding = innerPadding,
                sandwiches = sampleSandwiches,
                onSandwichClick = {},
            )
        }
    }
}
*/
