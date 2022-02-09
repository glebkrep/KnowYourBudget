package com.glbgod.knowyourbudget.core.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object PreferencesProvider {
    lateinit var preferences: SharedPreferences

    fun init(application: Application) {
        preferences = application.getSharedPreferences("budget", Context.MODE_PRIVATE)
    }

    fun getCycleStartTime(): Long {
        return preferences.getLong(
            CYCLE_START_TIME,
            0L
        )
    }

    fun saveCycleStartTime(time: Long) =
        preferences.edit().putLong(CYCLE_START_TIME, time).apply()

    fun saveRestartMoney(money: Int) {
        preferences.edit().putInt(STABLE_INCOME, money).apply()
    }

    fun getRestartMoney(): Int {
        return preferences.getInt(
            STABLE_INCOME,
            0
        )
    }

    fun saveMonthStartBalance(money: Int) {
        preferences.edit().putInt(MONTH_START_BALACNE, money).apply()
    }

    fun getMonthStartBalance(): Int {
        return preferences.getInt(
            MONTH_START_BALACNE,
            0
        )
    }

    fun isFirstStart(): Boolean {
        return preferences.getBoolean("is_first", true)
    }

    fun saveNotFirstStart() {
        preferences.edit().putBoolean("is_first", false).apply()
    }

    private const val CYCLE_START_TIME = "cycle_start_time"
    private const val STABLE_INCOME = "stable_income"
    private const val MONTH_START_BALACNE = "month_start_balance"
}