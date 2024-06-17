package com.d479.xpenses.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DonutSmall
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RequestPage
import androidx.compose.material.icons.outlined.DonutSmall
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.RequestPage
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.d479.xpenses.navigation.Screens

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val badgeCounter: Int? = null,
    val route: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Navbar(
    modifier: Modifier,
    navController: NavController
) {
    val items = listOf(
        NavItem(
            label = "Expenses",
            selectedIcon = Icons.Filled.RequestPage,
            unselectedIcon = Icons.Outlined.RequestPage,
            hasNews = false,
            route = Screens.Expenses.route
        ),
        NavItem(
            label = "Map",
            selectedIcon = Icons.Filled.Map,
            unselectedIcon = Icons.Outlined.Map,
            hasNews = false,
            route = Screens.Map.route
        ),
        NavItem(
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            route = Screens.Home.route
        ),
        NavItem(
            label = "Split",
            selectedIcon = Icons.Filled.People,
            unselectedIcon = Icons.Outlined.People,
            hasNews = true,
            badgeCounter = 2,
            route = Screens.Split.route
        ),
        NavItem(
            label = "Analytics",
            selectedIcon = Icons.Filled.DonutSmall,
            unselectedIcon = Icons.Outlined.DonutSmall,
            hasNews = false,
            route = Screens.Analytics.route
        )
    )


    NavigationBar(modifier = Modifier) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = navController.currentDestination?.route == item.route,
                onClick = {
                    navController.navigate(item.route)
                },
                icon = {
                    BadgedBox(
                        badge =
                        {
                            if (item.badgeCounter != null) {
                                Badge {
                                    Text(text = item.badgeCounter.toString())
                                }
                            } else if (item.hasNews) {
                                Badge()
                            }

                        }) {
                        Icon(
                            imageVector =
                            if (navController.currentDestination?.route == item.route) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.label
                        )
                    }
                },
                label = {
                    Text(text = item.label)
                },
            )
        }
    }
}