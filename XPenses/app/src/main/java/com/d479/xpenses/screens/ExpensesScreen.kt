package com.d479.xpenses.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.d479.xpenses.HomeScreenViewModel
import com.d479.xpenses.ui.components.Navbar

@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeScreenViewModel
) {

    val user by viewModel.user.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()

    Scaffold(
        bottomBar = {
            Navbar(modifier = modifier, navController = navController)
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply the padding here
            contentAlignment = Alignment.Center
        ) {
//            Text(text = "Expenses Page")
//            Row(
//                modifier = modifier.padding(16.dp),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.End
//            ) {
//
//                Text(text = "User: ${user?.name}")
//                AsyncImage(
//                    model = user?.photoUrl,
//                    contentDescription = "Avatar",
//                    modifier = modifier
//                        .size(40.dp)
//                        .clip(CircleShape)
//                )
//            }

            LazyColumn {
                items(allUsers) { user ->
                    Row {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = user.name,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = user.name)
                    }
                }
            }
        }
    }


}