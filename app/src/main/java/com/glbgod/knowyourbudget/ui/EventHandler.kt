package com.glbgod.knowyourbudget.ui

interface EventHandler<T> {
    fun handleEvent(event: T)
}