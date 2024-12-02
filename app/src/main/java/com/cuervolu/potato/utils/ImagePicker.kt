package com.cuervolu.potato.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.cuervolu.potato.utils.permissions.PermissionUtils

class ImagePicker(
    private val context: Context,
    private val photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val legacyLauncher: ManagedActivityResultLauncher<String, Uri?>,
    private val permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
    private val onImagePicked: (Uri) -> Unit
) {
    fun pickImage() {
        when {
            PermissionUtils.shouldShowPhotoPickerInstead(context) -> {
                // Usar el PhotoPicker moderno
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
            PermissionUtils.hasImagePermissions(context) -> {
                // Usar el selector legacy
                legacyLauncher.launch("image/*")
            }
            else -> {
                // Solicitar permisos primero
                permissionLauncher.launch(
                    PermissionUtils.getRequiredImagePermission()
                )
            }
        }
    }

    companion object {
        @Composable
        fun createImagePicker(
            context: Context,
            onImagePicked: (Uri) -> Unit,
            onPermissionDenied: () -> Unit = {}
        ): ImagePicker {
            val photoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickVisualMedia()
            ) { uri ->
                uri?.let(onImagePicked)
            }

            val legacyLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                uri?.let(onImagePicked)
            }

            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                if (isGranted) {
                    legacyLauncher.launch("image/*")
                } else {
                    onPermissionDenied()
                }
            }

            return remember(photoPickerLauncher, legacyLauncher, permissionLauncher, onImagePicked) {
                ImagePicker(
                    context,
                    photoPickerLauncher,
                    legacyLauncher,
                    permissionLauncher,
                    onImagePicked
                )
            }
        }
    }
}