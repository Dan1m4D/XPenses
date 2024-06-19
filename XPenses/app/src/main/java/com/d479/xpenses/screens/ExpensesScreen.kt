package com.d479.xpenses.screens

import HeaderActions
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d479.xpenses.ui.components.InvoiceItem
import com.d479.xpenses.ui.components.Navbar
import com.d479.xpenses.ui.components.TitleMessage
import com.d479.xpenses.viewModels.ExpensesViewModel

@Composable
fun ExpensesScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: ExpensesViewModel
) {
    val user by viewModel.user.collectAsState()
    val invoices by viewModel.invoices.collectAsState()
    val groupedInvoices = viewModel.getGroupedInvoices()



    Scaffold(
        topBar = {
            HeaderActions(
                profilePictureURL = user?.photoUrl,
                onSignOut = { /*TODO*/ },
                modifier = modifier,
            )
        },
        bottomBar = {
            Navbar(modifier = modifier, navController = navController)
        },
        modifier = modifier.fillMaxSize(),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(
                    rememberScrollState()),
                ) {
                    TitleMessage(modifier = modifier, text1 = "Expenses")

                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        if (groupedInvoices.isEmpty()) {
                            Box(
                                modifier = modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No invoices found",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        } else {
                            groupedInvoices.forEach { (date, invoices) ->
                                Text(
                                    text = viewModel.formatDate(date),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                invoices.forEach { invoice ->
                                    InvoiceItem(
                                        total = invoice.total,
                                        category = invoice.category?.name ?: "Expense",
                                        categoryColor = Color(android.graphics.Color.parseColor(invoice.category?.color)),
                                    )
                                }
                            }
                        }
                    }




                }
    }


}