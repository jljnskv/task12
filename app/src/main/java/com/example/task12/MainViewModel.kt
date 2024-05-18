package com.example.task12

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow<States>(States.Initial)
    val state = _state.asStateFlow()

    var currentSearchText: String = ""

    private var searchJob: Job? = null

    private fun search(searchText: String) {
        currentSearchText = searchText
        if (currentSearchText.length > MINIMUM_QUERY_LENGTH) {
            searchJob = viewModelScope.launch {
                //запоминаем запрос, чтобы выдать его в результате
                _state.value = States.Loading
                delay(LOADING_TIME)
                //на случай отмены запроса после нескольких вводов символов для длины запроса >3
                if (currentSearchText.isNotEmpty())
                    _state.value = States.Success(currentSearchText)
                else
                    _state.value = States.Initial
            }
        } else if (currentSearchText.isNotEmpty()) {
            searchJob?.cancel()
            _state.value = States.Initial
        } else {
            searchJob?.cancel()
            _state.value = States.Canceled
        }
    }

    fun onCancel(): Boolean {
        //currentSearchText = ""
        search("")
        return true
    }

    fun searchDebounced(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(DEBOUNCE_TIME)
            search(searchText)
        }
    }

    companion object {
        const val LOADING_TIME = 7_000L
        const val DEBOUNCE_TIME = 300L
        const val MINIMUM_QUERY_LENGTH = 3
    }
}