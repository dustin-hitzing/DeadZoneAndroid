package com.ghostcat.deadzone.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ghostcat.deadzone.R
import com.ghostcat.deadzone.Screen
import com.ghostcat.deadzone.viewmodels.MapViewModel
import com.ghostcat.deadzone.views.components.MapListItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapListPage(
    navController: NavController,
    modifier: Modifier,
    viewModel: MapViewModel
) {
    val testReports = viewModel.testReports.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.getReports()
    }

    LazyColumn(modifier = modifier
        .background(color = colorResource(R.color.primary_bg))
        .fillMaxSize()
        .padding(17.dp)) {
        item {
            Text("Test Reports",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(vertical = 10.dp))
        }

        items(testReports) {
            Column(modifier = Modifier.clickable {
                viewModel.selectTestReport(testReport = it)
                navController.navigate(Screen.Map.route)
            }) {
                MapListItem(testResult = it.testResults[0], duration = it.duration, passed = true)
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}