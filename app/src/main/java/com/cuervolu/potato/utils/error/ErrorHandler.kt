package com.cuervolu.potato.utils.error

import android.content.Context
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.cuervolu.potato.utils.state.AppError
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

class ErrorHandler(private val context: Context) {

    fun handleError(
        error: AppError,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        action: (() -> Unit)? = null
    ) {
        when (error) {
            is AppError.DatabaseError -> logger.error(error.cause) {
                "Database error: ${error.message}"
            }
            is AppError.StorageError -> logger.error(error.cause) {
                "Storage error: ${error.message}"
            }
            is AppError.ValidationError -> logger.warn {
                "Validation error: ${error.message}"
            }
            is AppError.UnexpectedError -> logger.error(error.cause) {
                "Unexpected error: ${error.message}"
            }

            is AppError.NetworkError -> TODO()
        }
        showErrorSnackbar(error, snackbarHostState, scope, action)
    }

    private fun showErrorSnackbar(
        error: AppError,
        snackbarHostState: SnackbarHostState,
        scope: CoroutineScope,
        action: (() -> Unit)? = null
    ) {
        val message = when (error) {
            is AppError.DatabaseError -> "Error al guardar los datos"
            is AppError.StorageError -> "Error al guardar el archivo"
            is AppError.ValidationError -> error.message
            is AppError.UnexpectedError -> "Ha ocurrido un error inesperado"
            is AppError.NetworkError -> TODO()
        }

        scope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                actionLabel = if (action != null) "Reintentar" else null,
                duration = SnackbarDuration.Long
            )
        }
    }
}