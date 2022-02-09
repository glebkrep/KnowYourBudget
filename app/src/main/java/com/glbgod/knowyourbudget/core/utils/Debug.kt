package com.glbgod.knowyourbudget.core.utils

import android.util.Log

object Debug {
    fun log(any: Any?) {
        Log.e("DEBUG:::", any.toString())
    }
}