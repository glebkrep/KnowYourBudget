package com.glbgod.knowyourbudget.core.utils

import com.glbgod.knowyourbudget.feature.db.data.ExpenseRegularity

object Utils {

    fun getDaysToWeekStart(): Int {
        val cycleStart = PreferencesProvider.getCycleStartTime()
        var time = cycleStart
        var weeksPassed = 0
        while (true) {
            weeksPassed += 1
            time = weeksPassed * cycleStart + 7 * 24 * 60 * 60 * 1000L
            if (time > System.currentTimeMillis()) {
                return System.currentTimeMillis().daysPassed(time).toInt()
            }
        }
    }

    fun getDaysToCycleRestart(): Int {
        val cycleEnd = getCycleRestartTimeLong()
        val timeDiff = PreferencesProvider.getCycleStartTime().daysPassed(cycleEnd)
        return timeDiff.toInt()
    }

    fun getCycleRestartTime(): String {
        val cycleStart = PreferencesProvider.getCycleStartTime()
        val cycleRestart =
            cycleStart + (24L * 60L * 60L * 1000L * ExpenseRegularity.DAILY.refillsInMonth)
        val now = System.currentTimeMillis()
        return now.daysPassed(cycleRestart).toString()
    }

    private fun getCycleRestartTimeLong(): Long {
        val cycleStart = PreferencesProvider.getCycleStartTime()
        return cycleStart + (24L * 60L * 60L * 1000L * ExpenseRegularity.DAILY.refillsInMonth)
    }
}