package com.example.apollographqltutorial.view.state

sealed class ViewState<out T : Any> {

    data class Success<out T : Any>(val result: T) : ViewState<T>()
    data class Error(val exception: Exception) : ViewState<Nothing>()
    object Loading : ViewState<Nothing>()

    val extractData: T?
        get() = when (this) {
            is Success -> result
            is Error -> null
            is Loading -> null
        }
}