package com.ghostcat.deadzone.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ghostcat.deadzone.R
import com.ghostcat.deadzone.Screen

@Composable
fun HomePage(
    navController: NavController,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primary_bg))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.deadzone_icon),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.deadzone_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable {
                            navController.navigate(Screen.MapList.route)
                        }
                )

            }
            Spacer(modifier = Modifier.height(100.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = "Connected\nor not connected?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(R.color.primary_text),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "That is the question.",
                    style = MaterialTheme.typography.headlineSmall,

                    )
                Spacer(modifier = Modifier.height(75.dp))
                Text(
                    text = "DeadZone detects and visualizes where you have and lose connection so you can stay connected with confidence.",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        Button(
            onClick = {
                navController.navigate(Screen.Testing.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.accent_color),
                contentColor = Color.White
            )
        ) {
            Text("Start Test")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    //HomePage(navController = NavController(), modifier = Modifier)
}