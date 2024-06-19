package com.d479.xpenses.screens

import HeaderActions
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d479.xpenses.R
import com.d479.xpenses.navigation.Screens
import com.d479.xpenses.ui.components.AnalyticsCard
import com.d479.xpenses.ui.components.InvoiceItem
import com.d479.xpenses.ui.components.Navbar
import com.d479.xpenses.ui.components.TitleMessage
import com.d479.xpenses.viewModels.HomeScreenViewModel
import java.util.Date

@Composable
fun HomeScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: HomeScreenViewModel,
    onSignOut: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val groupedInvoices = viewModel.getRecentGroupedInvoices()

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
                data = viewModel.getAnalyticsData(),
                barTitle = "Last 7 days"
            )
            if (groupedInvoices.isEmpty()) {
                Box(modifier = modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = "No invoices found", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                groupedInvoices.forEach { (date, invoices) ->
                    println("Date: ${Date(date.epochSeconds * 1000)}")
                    invoices.forEach { invoice ->
                        println("Invoice: ${invoice.total}")
                        println(
                            "Category: ${
                                viewModel.getCategoryById(
                                    invoice.categoryId
                                ).name
                            }"
                        )
                    }
                }

                groupedInvoices.forEach { (date, invoices) ->
                    Text(
                        text = viewModel.formatDate(date),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    invoices.forEach { invoice ->
                        val category = viewModel.getCategoryById(invoice.categoryId)
                        InvoiceItem(
                            total = invoice.total,
                            category = category.name,
                            categoryColor = Color(android.graphics.Color.parseColor(
                                if (category.color.isEmpty()) "#000000" else category.color
                            )),
                        )
                    }
                }
            }

        }

    }
}