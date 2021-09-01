package com.glbgod.knowyourbudget.utils

import android.text.format.DateUtils
import com.glbgod.knowyourbudget.expenses.data.Expense
import com.glbgod.knowyourbudget.expenses.data.ExpenseRegularity
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

fun Long.isToday(): Boolean {
    return DateUtils.isToday(this)
}

// function to return dates difference between two days
// todo test this
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

// difference in 'calendar weeks' between 'this' and now
// todo test this
fun Long.weeksPassed(weekStart: Long, toDate: Long): Int {
    val lastSyncTime = this
    val daysToWeekStart = Utils.getDaysToWeekStart(weekStart, lastSyncTime)

    var daysPassed = lastSyncTime.daysPassed(toDate)

    var weeksPassed = 0
    if (daysPassed >= daysToWeekStart) {
        daysPassed -= daysToWeekStart
        weeksPassed += 1
        weeksPassed += (daysPassed.toInt() / 7)
    }
    return weeksPassed
}

fun Float.getCloseRand(restart: Boolean = false): Float {
    val isIncrease = Random.nextBoolean()
    val randInt = Random.nextInt(0, 200)
    val rawModifier = (randInt.toFloat() / 1000f)
    val floatModifier =
        if (isIncrease) {
            1 + rawModifier
        } else {
            1 - rawModifier
        }
    if (floatModifier > 1f || !restart) {
        return this.getCloseRand(true)
    }
    return this * floatModifier
}

fun Long.toDateTime(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return formatter.format(calendar.time)
}


fun Long.plusDays(day: Int): Long {
    return this + (day * 24 * 60 * 60 * 1000)
}

fun List<Expense>.calculateLeftOver(stableIncome: Int): Int {
    var totalMoneyUsed = 0
    for (item in this) {
        if (item.id == 1) continue
        totalMoneyUsed += when (item.regularity) {
            ExpenseRegularity.DAILY.regularity -> {
                item.budget * 35
            }
            ExpenseRegularity.WEEKLY.regularity -> {
                item.budget * 5
            }
            ExpenseRegularity.MONTHLY.regularity -> {
                item.budget
            }
            else -> {
                throw Exception("Didn't consider ${item.regularity} regularity")
            }
        }
    }
    return stableIncome - totalMoneyUsed
}
