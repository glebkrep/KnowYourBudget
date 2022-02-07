package com.glbgod.knowyourbudget.utils

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object PreferencesProvider {
    lateinit var preferences: SharedPreferences

    fun init(application: Application) {
        preferences = application.getSharedPreferences("budget", Context.MODE_PRIVATE)
    }

    fun getLastUpdateTime() =
        preferences.getLong(
            LAST_TIME_UPDATED,
            0L
        )

    fun saveLastUpdateTime(time: Long) =
        preferences.edit().putLong(LAST_TIME_UPDATED, time).apply()


    fun getCycleStartTime(): Long {
        return preferences.getLong(
            CYCLE_START_TIME,
            0L
        )
    }

    fun saveCycleStartTime(time: Long) =
        preferences.edit().putLong(CYCLE_START_TIME, time).apply()

    fun saveStableIncome(money: Int) {
        preferences.edit().putInt(STABLE_INCOME, money).apply()
    }

    fun getStableIncome(): Int {
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

    fun saveFirstStarted(boolean: Boolean){
        preferences.edit().putBoolean(FIRST_STARTED_ALREADY,boolean).apply()
    }

    fun getFirstStarted():Boolean{
        return preferences.getBoolean(
            FIRST_STARTED_ALREADY,
            false
        )
    }

    private const val LAST_TIME_UPDATED = "last_time_updated"
    private const val CYCLE_START_TIME = "cycle_start_time"
    private const val STABLE_INCOME = "stable_income"
    private const val MONTH_START_BALACNE = "month_start_balance"
    private const val FIRST_STARTED_ALREADY = "WAS_LAST_FROM_FIRST_START"


}