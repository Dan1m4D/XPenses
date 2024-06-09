package com.d479.xpenses

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d479.xpenses.navigation.Screens
import com.d479.xpenses.screens.AnalyticsPage
import com.d479.xpenses.screens.ExpensesPage
import com.d479.xpenses.screens.HomePage
import com.d479.xpenses.screens.MapPage
import com.d479.xpenses.screens.SplitPage
import com.d479.xpenses.ui.theme.XPensesTheme

class MainActivity : ComponentActivity() {
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
                    NavHost(navController = navController, startDestination = "home") {
                        composable(Screens.Home.route) {
                            HomePage(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Expenses.route) {
                            ExpensesPage(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Map.route) {
                            MapPage(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Analytics.route) {
                            AnalyticsPage(modifier = Modifier, navController = navController)
                        }
                        composable(Screens.Split.route) {
                            SplitPage(modifier = Modifier, navController = navController)
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

