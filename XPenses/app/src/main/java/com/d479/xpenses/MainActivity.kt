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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.d479.xpenses.screens.SignInScreen
import com.d479.xpenses.screens.SplitScreen
import com.d479.xpenses.signIn.GoogleAuthUiClient
import com.d479.xpenses.signIn.SignInViewModel
import com.d479.xpenses.ui.theme.XPensesTheme
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
                // A surface container using the 'background' color from the theme
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

                            // check if the user is signed in
                            LaunchedEffect(key1 = Unit) {
                                if (googleAuthUiClient.getSignedInUser() != null) {
                                    navController.navigate(Screens.Home.route)
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

                                    navController.navigate(Screens.Home.route)
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
                            HomeScreen(
                                modifier = Modifier,
                                navController = navController,
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUiClient.signOut()

                                        Toast.makeText(
                                            applicationContext,
                                            "Signed out successful",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        navController.navigate(Screens.SignIn.route)
                                    }
                                },
                                userData = googleAuthUiClient.getSignedInUser()
                            )
                        }
                        composable(Screens.Expenses.route) {
                            ExpensesScreen(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Map.route) {
                            MapScreen(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Analytics.route) {
                            AnalyticsScreen(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Split.route) {
                            SplitScreen(modifier = Modifier, navController = navController)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

