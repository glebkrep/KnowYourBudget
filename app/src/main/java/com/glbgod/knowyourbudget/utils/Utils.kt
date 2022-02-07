package com.glbgod.knowyourbudget.utils

import androidx.compose.ui.graphics.Color

object Utils {
    fun getDaysToWeekStart(weekStart: Long, lastSyncTime: Long): Long {
        val diffDays = weekStart.daysPassed(lastSyncTime)
        return 7 - (diffDays % 7)
    }

    fun generateColors(categoryNames: List<String>, originalColor: Color): Map<String, Color> {
        val colorMap = mutableMapOf<String, Color>()
        for (category in categoryNames) {
            colorMap.put(category, getSimilarColor(originalColor))
        }
        return colorMap
    }

    private fun getSimilarColor(color: Color): Color {
        return color
    }

    fun getDaysToWeekStart(): String {
        val cycleStart = PreferencesProvider.getCycleStartTime()
        val lastUpdate = PreferencesProvider.getLastUpdateTime()
        val daysToWeekStart = getDaysToWeekStart(cycleStart, lastUpdate)
        return daysToWeekStart.toString()
    }

    fun getCycleRestartTime(): String {
        val cycleStart = PreferencesProvider.getCycleStartTime()
        val cycleRestart = cycleStart + (24L * 60L * 60L * 1000L.dailyToMonthly())
        val now = System.currentTimeMillis()
        return now.daysPassed(cycleRestart).toString()
    }

    fun getCycleRestartTimeLong(): Long {
        val cycleStart = PreferencesProvider.getCycleStartTime()
        return cycleStart + (24L * 60L * 60L * 1000L.dailyToMonthly())
    }
}