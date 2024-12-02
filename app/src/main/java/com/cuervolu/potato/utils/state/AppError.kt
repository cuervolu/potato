package com.cuervolu.potato.utils.state

sealed class AppError {
    data class DatabaseError(val message: String, val cause: Throwable? = null) : AppError()
    data class NetworkError(val message: String, val cause: Throwable? = null) : AppError()
    data class StorageError(val message: String, val cause: Throwable? = null) : AppError()
    data class ValidationError(val message: String) : AppError()
    data class UnexpectedError(val message: String, val cause: Throwable? = null) : AppError()
}