package com.d479.xpenses

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
import com.d479.xpenses.viewModels.HomeScreenViewModel
import com.d479.xpenses.viewModels.MapViewModel
import com.example.compose.XPensesTheme
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                            SignInScreen(
                                state = state,
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

                            val mapViewModel = viewModel<MapViewModel>()

                            MapScreen(
                                modifier = Modifier,
                                navController = navController,
                                viewModel = mapViewModel
                            )
                        }
                        composable(Screens.Analytics.route) {
                            AnalyticsScreen(modifier = Modifier, navController = navController)
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

