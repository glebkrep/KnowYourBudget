package com.glbgod.knowyourbudget.feature.db.data

import com.glbgod.knowyourbudget.ui.theme.MyColors
import com.glbgod.knowyourbudget.ui.theme.RegularityStyle

enum class ExpenseRegularity(
    val regularity: Int,
    val refillsInMonth: Int,
    val text: String,
    val regularityStyle: RegularityStyle
) {
    DAILY(
        0, refillsInMonth = 35, "Ежедневные", RegularityStyle(
            MyColors.DailyColor,
            MyColors.DailyFont,
        )
    ),
    WEEKLY(
        1, refillsInMonth = 5, "Еженедельные", RegularityStyle(
            MyColors.WeeklyColor,
            MyColors.WeeklyFont,
        )
    ),
    MONTHLY(
        2, refillsInMonth = 1, "Месячные", RegularityStyle(
            MyColors.MonthlyColor,
            MyColors.MonthlyFont,
        )
    )
}

fun expenseRegularityByRegularityInt(regularity: Int): ExpenseRegularity {
    return when (regularity) {
        ExpenseRegularity.DAILY.regularity -> ExpenseRegularity.DAILY
        ExpenseRegularity.WEEKLY.regularity -> ExpenseRegularity.WEEKLY
        else -> ExpenseRegularity.MONTHLY
    }
}