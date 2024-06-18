package com.d479.xpenses

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d479.xpenses.navigation.Screens
import com.d479.xpenses.screens.AnalyticsScreen
import com.d479.xpenses.screens.ExpensesScreen
import com.d479.xpenses.screens.HomeScreen
import com.d479.xpenses.screens.MapScreen
import com.d479.xpenses.screens.ScanScreen
import com.d479.xpenses.screens.SignInScreen
import com.d479.xpenses.screens.SplitScreen
import com.d479.xpenses.signIn.GoogleAuthUiClient
import com.d479.xpenses.signIn.SignInViewModel
import com.d479.xpenses.viewModels.AnalyticsViewModel
import com.d479.xpenses.viewModels.CatViewModel
import com.d479.xpenses.viewModels.HomeScreenViewModel
import com.d479.xpenses.viewModels.MapViewModel
import com.example.compose.XPensesTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // set up fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            XPensesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screens.SignIn.route
                    ) {
                        composable(Screens.SignIn.route) {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate("home")
                                    println("User already signed in -> " + googleAuthUiClient.getSignedInUser()!!)
                                    viewModel.loginUser(googleAuthUiClient.getSignedInUser()!!)
                                }
                            }

                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUiClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                            viewModel.loginUser(signInResult.data!!)
                                        }
                                    }
                                }
                            )

                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign in successful",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("home")
                                    viewModel.resetState()
                                }
                            }
                            val catviewModel = viewModel<CatViewModel>()
                            SignInScreen(
                                state = state,
                                viewModel = catviewModel,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntentSender = googleAuthUiClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntentSender ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }

                        composable(Screens.Home.route) {
                            val localContext = LocalContext.current
                            val homeViewModel = viewModel<HomeScreenViewModel>()

                            HomeScreen(
                                modifier = Modifier,
                                navController = navController,
                                viewModel = homeViewModel,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()

                                        Toast.makeText(
                                            localContext,
                                            "Signed out successful",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        homeViewModel.resetState()
                                        navController.navigate(Screens.SignIn.route)
                                    }
                                },
                            )
                        }
                        composable(Screens.Expenses.route) {
                            ExpensesScreen(
                                modifier = Modifier,
                                navController = navController,

                                )
                        }
                        composable(Screens.Map.route) {
                            val localContext = LocalContext.current
                            val mapViewModel: MapViewModel = viewModel()

                            // ask for location permission
                            val locationPermissions = rememberMultiplePermissionsState(
                                permissions = listOf(
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION
                                )
                            )

                            //1. when the app get launched for the first time
                            LaunchedEffect(true){
                                locationPermissions.launchMultiplePermissionRequest()

                                // get the last known location
                                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                    if (location != null) {
                                        mapViewModel.updateLocation(location)
                                    } else {
                                        Toast.makeText(
                                            localContext,
                                            "Location not found",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        locationPermissions.launchMultiplePermissionRequest()
                                    }
                                }
                            }



                            MapScreen(
                                modifier = Modifier,
                                navController = navController,
                                viewModel = mapViewModel
                            )
                        }
                        composable(Screens.Analytics.route) {
                            val analyticsViewModel = viewModel<AnalyticsViewModel>()

                            AnalyticsScreen(modifier = Modifier, navController = navController, viewModel = analyticsViewModel)
                        }
                        composable(Screens.Split.route) {
                            SplitScreen(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Scan.route) {
                            ScanScreen(modifier = Modifier, navController = navController)
                        }
                    }

                }
            }
        }
    }
}


