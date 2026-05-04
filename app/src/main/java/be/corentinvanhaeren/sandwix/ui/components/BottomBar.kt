package be.corentinvanhaeren.sandwix.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import be.corentinvanhaeren.sandwix.ui.navigation.MainTab
import be.corentinvanhaeren.sandwix.ui.navigation.Route

@Composable
internal fun SandwixBottomBar(
    selectedTab: MainTab,
    onTabSelected: (Route) -> Unit,
) {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab.route) },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                    )
                }
            }
        }

