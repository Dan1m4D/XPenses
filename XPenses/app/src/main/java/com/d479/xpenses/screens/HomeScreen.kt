package com.d479.xpenses.screens

import HeaderActions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d479.xpenses.ui.components.Navbar
import com.d479.xpenses.R
import com.d479.xpenses.signIn.UserData
import kotlin.math.sign


@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavHostController,
    onSignOut: () -> Unit,
    userData: UserData?
) {


    Scaffold(
        bottomBar = {
            Navbar(modifier = modifier, navController = navController)
        },
        topBar = {
            HeaderActions(profilePictureURL = userData?.profilePictureURL)
        },
        floatingActionButton = {
            Button(
                onClick = { /*TODO*/ },
                shape = CircleShape,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(4.dp, CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Add",
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply the padding here
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Hello ${userData?.username}!")
                Text(text = "Home Page")
                Button(onClick = onSignOut) {
                    Text("Sign Out")
                }
            }

        }
    }
}
