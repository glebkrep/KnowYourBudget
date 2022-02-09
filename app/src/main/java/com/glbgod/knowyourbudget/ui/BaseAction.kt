package com.glbgod.knowyourbudget.ui

sealed class BaseAction {
    object None : BaseAction()
    object GoBack : BaseAction()
    data class GoAway(val route: String) : BaseAction()
}
