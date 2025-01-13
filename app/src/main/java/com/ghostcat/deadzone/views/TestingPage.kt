package com.ghostcat.deadzone.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ghostcat.deadzone.R
import com.ghostcat.deadzone.viewmodels.TestingViewModel

@Composable
fun TestingPage(navController: NavController, modifier: Modifier) {
    val viewModel: TestingViewModel = hiltViewModel()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primary_bg))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(
                    painter = painterResource(id = R.drawable.deadzone_icon),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )

            }
            Spacer(modifier = Modifier.height(100.dp))
            Column(modifier = Modifier.fillMaxWidth()
                .padding(10.dp)) {
                Text(
                    text = "Test in Progress",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(R.color.primary_text),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(35.dp))
                Text(
                    text = "Please walk slowly and keep phone from turning off while testing connectivity",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        if (isConnected) {
            Text(text = "Connected")
        } else {
            Text(text = "Not Connected")
        }

        Button(onClick = {

        },
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.accent_color),
                contentColor = Color.White
            )) {
            Text("End Test")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestingPagePreview() {
    //TestingPage()
}