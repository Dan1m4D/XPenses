package com.d479.xpenses.screens

import HeaderActions
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d479.xpenses.R
import com.d479.xpenses.navigation.Screens
import com.d479.xpenses.ui.components.AnalyticsCard
import com.d479.xpenses.ui.components.Navbar
import com.d479.xpenses.ui.components.TitleMessage
import com.d479.xpenses.viewModels.HomeScreenViewModel

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: HomeScreenViewModel,
    onSignOut: () -> Unit
) {
    val user by viewModel.user.collectAsState()

    if (user == null) {
        navController.navigate(Screens.SignIn.route)
        onSignOut()
    }

    Scaffold(
        bottomBar = {
            Navbar(modifier = modifier, navController = navController)
        },
        topBar = {
            HeaderActions(
                modifier = modifier,
                profilePictureURL = user?.photoUrl,
                onSignOut = onSignOut
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.Scan.route) },
                modifier = Modifier.size(75.dp),
                shape = CircleShape,
                containerColor = Color(0xFFBCF0B4),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.scan),
                    contentDescription = "Scan",
                    modifier = Modifier.size(50.dp)
                )

            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
            .fillMaxSize(),
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            TitleMessage(modifier = modifier, text1 = "Hello ", text2 = viewModel.getName())
            AnalyticsCard(
                data = listOf(
                    100.00,
                    50.00,
                    23.00,
                    123.00,
                    200.00,
                ),
                barTitle = "Last 7 days"
            )

        }

    }
}