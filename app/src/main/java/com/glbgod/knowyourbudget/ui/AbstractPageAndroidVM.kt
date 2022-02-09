package com.glbgod.knowyourbudget.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class AbstractPageAndroidVM<EVENT, STATE, ACTION>(
    application: Application,
    val defaultAction: ACTION
) : AndroidViewModel(application), EventHandler<EVENT> {
    private val _state: MutableLiveData<STATE> = MutableLiveData()
    val state: LiveData<STATE> = _state

    private val _action: MutableLiveData<ACTION> = MutableLiveData()
    val action: LiveData<ACTION> = _action

    protected fun postAction(action: ACTION) {
        _action.postValue(action!!)
        viewModelScope.launch {
            delay(100)
            _action.postValue(defaultAction!!)
        }
    }

    protected fun getCurrentState(): STATE? {
        return _state.value
    }

    protected fun getCurrentStateNotNull(): STATE {
        return _state.value ?: throw Exception("Current state is not expected to be null")
    }

    protected fun postState(state: STATE) {
        _state.postValue(state!!)
    }
}