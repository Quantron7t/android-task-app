package com.example.todoapp.ancillary

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    fun getLocalFormattedTime(epochTime : Long) : String{
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(epochTime), ZoneId.of("UTC"))
        val deviceTimeZone = ZoneId.systemDefault()

        val zonedDateTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(deviceTimeZone)
        val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy, HH:mm")
        return zonedDateTime.format(formatter)
    }
}