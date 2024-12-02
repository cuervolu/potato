package com.cuervolu.potato.utils.state

sealed class UIState<out T> {
    data object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val error: AppError) : UIState<Nothing>()
}