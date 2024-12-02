package com.cuervolu.potato.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cuervolu.potato.R

/**
 * Diálogo reutilizable para solicitar permisos
 *
 * @param title Título del diálogo
 * @param description Descripción del permiso solicitado
 * @param confirmButtonText Texto del botón de confirmación
 * @param dismissButtonText Texto del botón de cancelación
 * @param onConfirm Callback ejecutado cuando el usuario confirma
 * @param onDismiss Callback ejecutado cuando el usuario cancela o cierra el diálogo
 */
@Composable
fun PermissionRequestDialog(
    title: String,
    description: String,
    confirmButtonText: String = stringResource(R.string.allow),
    dismissButtonText: String = stringResource(R.string.not_now),
    icon: @Composable (() -> Unit)? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon,
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissButtonText)
            }
        }
    )
}

/**
 * Valores predefinidos para diferentes tipos de permisos
 */
object PermissionDialogData {
    data class DialogContent(
        val title: String,
        val description: String
    )

    @Composable
    fun getStoragePermissionContent() = DialogContent(
        title = stringResource(R.string.storage_permission_title),
        description = stringResource(R.string.storage_permission_description)
    )

    @Composable
    fun getCameraPermissionContent() = DialogContent(
        title = stringResource(R.string.camera_permission_title),
        description = stringResource(R.string.camera_permission_description)
    )

    @Composable
    fun getLocationPermissionContent() = DialogContent(
        title = stringResource(R.string.location_permission_title),
        description = stringResource(R.string.location_permission_description)
    )
}