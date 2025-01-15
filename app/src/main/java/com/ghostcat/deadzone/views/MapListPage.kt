package com.ghostcat.deadzone.views

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ghostcat.deadzone.viewmodels.MapViewModel
import com.ghostcat.deadzone.views.components.MapListItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapListPage(
    navController: NavController,
    modifier: Modifier,
) {
    val viewModel: MapViewModel = hiltViewModel()
    val testReports = viewModel.testReports.collectAsStateWithLifecycle().value
    LazyColumn(modifier = modifier
        .fillMaxSize()
        .padding(17.dp)) {
        item {
            Text("Test Reports", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(vertical = 10.dp))
        }

        items(testReports) {
            MapListItem(testResult = it.testResults[0], duration = it.duration, passed = true)
            Spacer(modifier = Modifier.height(15.dp))
        }
    }
}