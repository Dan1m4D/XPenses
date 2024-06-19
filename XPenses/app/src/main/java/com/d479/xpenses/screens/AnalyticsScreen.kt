package com.d479.xpenses.screens

import HeaderActions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.d479.xpenses.ui.components.AnalyticsCard
import com.d479.xpenses.ui.components.FilterAnalytics
import com.d479.xpenses.ui.components.Navbar
import com.d479.xpenses.ui.components.PieGraph
import com.d479.xpenses.ui.components.SubtitleMessage
import com.d479.xpenses.ui.components.TitleMessage
import com.d479.xpenses.viewModels.AnalyticsViewModel


@Composable
fun AnalyticsScreen(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: AnalyticsViewModel
) {
    val user by viewModel.user.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val filterOptions by viewModel.filterOptions.collectAsState()
    val isDialogOpen by viewModel.isDialogOpen.collectAsState()
    val filteredInvoices by viewModel.filteredInvoices.collectAsState()

    val dailyTotal = viewModel.invoicesToDoubleList(filteredInvoices)
    val invoicesByCategory = viewModel.invoicesByCategory(filteredInvoices)


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
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TitleMessage(
                    modifier = modifier,
                    text1 = "Your ",
                    text2 = "stats"
                )

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

            AnalyticsCard(
                data = dailyTotal,
                barTitle = selectedFilter
            )

            Spacer(modifier = modifier.size(16.dp))

            SubtitleMessage(
                modifier = modifier.padding(4.dp),
                text1 = "Details",
                color = MaterialTheme.colorScheme.secondary
            )
            PieGraph(data = invoicesByCategory, modifier = modifier.fillMaxWidth())

        }
    }
}
