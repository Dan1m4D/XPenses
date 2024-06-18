
package com.d479.xpenses.screens

import HeaderActions
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d479.xpenses.ui.components.Navbar
import com.d479.xpenses.viewModels.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: MapViewModel,
) {
    val user by viewModel.user.collectAsState()
    val properties = viewModel.properties.value
    val uiSettings = viewModel.uiSettings.value
    val userLocation = viewModel.getCoordinates()

    LaunchedEffect(userLocation) {
        println("User location: ${viewModel.getCoordinates()}")
    }


    Scaffold(
        topBar = {
            HeaderActions(
                profilePictureURL = user?.photoUrl,
                onSignOut = { /*TODO*/ },
                modifier = modifier,
                bgColor = Color.Transparent
            )
        },
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
            Box(Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    properties = properties,
                    uiSettings = uiSettings,
                    cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(
                                viewModel.getCoordinates().latitude,
                                viewModel.getCoordinates().longitude
                            ),
                            15f
                        )

                    }

                ) {
                    MarkerComposable(
                        state = MarkerState(
                            position = LatLng(
                                viewModel.getCoordinates().latitude,
                                viewModel.getCoordinates().longitude
                            )
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "maker",
                            modifier = modifier.size(50.dp),
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}
