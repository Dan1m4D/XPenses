package com.d479.xpenses.ui.components

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.d479.xpenses.R
import com.d479.xpenses.navigation.Screens

data class NavItem(
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
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
            selectedIcon = R.drawable.request_page_filled,
            unselectedIcon = R.drawable.request_page_oulined,
            hasNews = false,
            route = Screens.Expenses.route
        ),
        NavItem(
            label = "Map",
            selectedIcon = R.drawable.map_filled,
            unselectedIcon = R.drawable.map_outlined,
            hasNews = false,
            route = Screens.Map.route
        ),
        NavItem(
            label = "Home",
            selectedIcon = R.drawable.home_filled,
            unselectedIcon = R.drawable.home_outlined,
            hasNews = false,
            route = Screens.Home.route
        ),
        NavItem(
            label = "Split",
            selectedIcon = R.drawable.divider_filled,
            unselectedIcon = R.drawable.divider_filled,
            hasNews = true,
            badgeCounter = 2,
            route = Screens.Split.route
        ),
        NavItem(
            label = "Analytics",
            selectedIcon = R.drawable.donut_filled,
            unselectedIcon = R.drawable.donut_outlined,
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
                            painter = painterResource(
                                id = if (navController.currentDestination?.route == item.route) item.selectedIcon else item.unselectedIcon
                            ),
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