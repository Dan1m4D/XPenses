package com.d479.xpenses.screens

import HeaderActions
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import com.d479.xpenses.ui.components.FilterAnalytics
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

    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val isDialogOpen by viewModel.isDialogOpen.collectAsState()
    val filteredInvoices by viewModel.filteredInvoices.collectAsState()
    val groupedInvoices = viewModel.getGroupedInvoices(filteredInvoices)


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
                    rememberScrollState()
                ),
        ) {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TitleMessage(modifier = modifier, text1 = "Expenses")
                Button(
                    onClick = viewModel::onDialogOpen,
                    modifier = modifier
                ) {
                    Icon(
                        imageVector = Icons.Filled.FilterList,
                        contentDescription = "Filter"
                    )
                    Text("Filter")
                }
            }

            if (isDialogOpen) {
                FilterAnalytics(
                    modifier = Modifier.padding(16.dp),
                    options = filterOptions,
                    onOptionSelected = viewModel::onOptionSelected,
                    selectedOption = selectedFilter,
                    onDesmissionRequest = viewModel::onDialogClose
                )
            }

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
                            Log.d("ExpensesScreen", "invoice: $invoice")
                            val category = viewModel.getCategoryById(invoice.categoryId)
                            InvoiceItem(
                                total = invoice.total,
                                category = category.name,
                                categoryColor = Color(
                                    android.graphics.Color.parseColor(
                                        if (category.color.isEmpty()) "#000000" else category.color
                                    )
                                ),
                            )
                        }
                    }
                }
            }


        }
    }


}