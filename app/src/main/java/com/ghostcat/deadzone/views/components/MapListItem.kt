package com.ghostcat.deadzone.views.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ghostcat.deadzone.R
import com.ghostcat.deadzone.models.TestResult
import com.ghostcat.deadzone.toFormattedDate
import com.ghostcat.deadzone.toFormattedTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapListItem(testResult: TestResult, duration: Long, passed: Boolean) {
    val locale = testResult.geoLocation?.addressInfo?.locality ?: "Nowhere"
    val country = testResult.geoLocation?.addressInfo?.countryName ?: "Nowhere"
    val date = testResult.geoLocation?.timestamp?.toFormattedDate()
    val time = testResult.geoLocation?.timestamp?.toFormattedTime()
    val formattedMinutes = ((duration / 1000) / 60)
    val formattedSeconds = (duration / 1000)

    Box(modifier = Modifier.wrapContentHeight()
        .fillMaxWidth()
        .background(shape = RoundedCornerShape(10.dp), color = colorResource(R.color.brand_gray))
        .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text(locale, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight(600))
                if (passed) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Passed",
                        tint = colorResource(R.color.positive)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Failed",
                        tint = colorResource(R.color.negative)
                    )
                }

            }
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                Text(date.toString())
                Text(time.toString())
                if (formattedMinutes < 1) {
                    Text("$formattedSeconds sec")
                } else {
                    Text("$formattedMinutes min")
                }
            }
        }
    }
}