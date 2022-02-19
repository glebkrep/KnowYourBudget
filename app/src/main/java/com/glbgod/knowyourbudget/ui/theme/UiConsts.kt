package com.glbgod.knowyourbudget.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.glbgod.knowyourbudget.R
import com.glbgod.knowyourbudget.data.IconColors

object UiConsts {
    val padding = 4.dp

    val iconsMap = mapOf<Int, IconColors>(
        Pair(
            R.drawable.ic_car,
            IconColors(
                backgroundColor = Color(0xFFF4F5B7),
                textColor = Color(0xFFBEC113)
            )
        ),
        Pair(
            R.drawable.ic_entertainment,
            IconColors(
                backgroundColor = Color(0xFFFCE3C0),
                textColor = Color(0xFFFFA53B)
            ),
        ),
        Pair(
            R.drawable.ic_other,
            IconColors(
                backgroundColor = Color(0xFFF1F1F1),
                textColor = Color(0xFF787878)
            ),
        ),
        Pair(
            R.drawable.ic_entertainment,
            IconColors(
                backgroundColor = Color(0xFFFCE3C0),
                textColor = Color(0xFFFFA53B)
            ),
        ),
        Pair(
            R.drawable.ic_home,
            IconColors(
                backgroundColor = Color(0xFFD4EBDD),
                textColor = Color(0xFF08C397)
            ),
        ),
        Pair(
            R.drawable.ic_food,
            IconColors(
                backgroundColor = Color(0xFFD1E8C8),
                textColor = Color(0xFF2CA43F)
            ),
        ),
        Pair(
            R.drawable.ic_inter,
            IconColors(
                backgroundColor = Color(0xFFD1EDFB),
                textColor = Color(0xFF2AACED)
            ),
        ),
        Pair(
            R.drawable.ic_love,
            IconColors(
                backgroundColor = Color(0xFFFBDADB),
                textColor = Color(0xFFFF70AA)
            ),
        ),
        Pair(
            R.drawable.ic_travel,
            IconColors(
                backgroundColor = Color(0xFFD3E0F1),
                textColor = Color(0xFF5D6FC3)
            ),
        ),
        Pair(
            R.drawable.ic_health,
            IconColors(
                backgroundColor = Color(0xFFFCD9CC),
                textColor = Color(0xFFFF5F61)
            ),
        ),
        Pair(
            R.drawable.ic_shop,
            IconColors(
                backgroundColor = Color(0xFFE9D5DB),
                textColor = Color(0xFFC246CD)
            ),
        ),
    )

}