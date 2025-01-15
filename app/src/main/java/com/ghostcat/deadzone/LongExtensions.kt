package com.ghostcat.deadzone

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toFormattedDate(
    pattern: String = "MM/dd/yyyy",
    locale: Locale = Locale.getDefault()
) : String {
    val instant = Instant.ofEpochMilli(this)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return dateTime.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toFormattedTime(
    pattern: String = "hh:mm a",
    locale: Locale = Locale.getDefault()
) : String {
    val instant = Instant.ofEpochMilli(this)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(pattern, locale)
    return dateTime.format(formatter)
}