package com.glbgod.knowyourbudget.core.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Long.isToday(): Boolean {
    return DateUtils.isToday(this)
}

fun Long.daysPassed(toDate: Long): Long {
    val oldDate = Calendar.getInstance().apply {
        timeInMillis = this@daysPassed
        set(Calendar.MILLISECOND, 1)
        set(Calendar.SECOND, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.HOUR_OF_DAY, 0)
    }
    val newDate = Calendar.getInstance().apply {
        timeInMillis = toDate
        set(Calendar.MILLISECOND, 1)
        set(Calendar.SECOND, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.HOUR_OF_DAY, 0)
    }
    return TimeUnit.DAYS.convert(
        (newDate.timeInMillis - oldDate.timeInMillis),
        TimeUnit.MILLISECONDS
    )
}

fun Long.toDateTime(): String {
    val formatter = SimpleDateFormat("hh:mm (dd.MM)")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return formatter.format(calendar.time)
}

// todo rewrite
fun Int?.toBeautifulString(): String {
    if (this == null) return ""
    return if (this > 9999) {
        val remainder = (this % 1000).toString()
        val remainderZerosString = when (3 - remainder.length) {
            1 -> "0"
            2 -> "00"
            3 -> "000"
            else -> ""
        }
        val mainPart = this / 1000
        ("$mainPart $remainderZerosString$remainder")
    } else this.toString()
}

fun String?.firstNOrNull(n:Int):String{
    if (this == null) return ""
    val limit = n
    var finalString = ""
    for (i in 0 until minOf(this.length,n)){
        finalString+=this[i]
    }
    return finalString
}