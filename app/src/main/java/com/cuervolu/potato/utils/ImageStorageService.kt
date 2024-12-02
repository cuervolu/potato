package com.cuervolu.potato.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageStorageService(private val context: Context) {
    private val logger = KotlinLogging.logger {}

    companion object {
        private const val IMAGES_DIR = "note_covers"
    }

    private val imagesDir: File
        get() = File(context.filesDir, IMAGES_DIR).also {
            if (!it.exists()) it.mkdirs()
        }

    fun saveImage(uri: Uri): Result<String> = try {
        val fileName = "${UUID.randomUUID()}.jpg"
        val destinationFile = File(imagesDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(input, null, options)

            val maxDimension = 1024
            val scale = Math.min(
                options.outWidth / maxDimension,
                options.outHeight / maxDimension
            ).coerceAtLeast(1)

            context.contentResolver.openInputStream(uri)?.use { newInput ->
                val bitmap = BitmapFactory.Options().apply {
                    inSampleSize = scale
                }.let { scaledOptions ->
                    BitmapFactory.decodeStream(newInput, null, scaledOptions)
                }

                FileOutputStream(destinationFile).use { output ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 85, output)
                }
                bitmap?.recycle()
            }
        }

        Result.success(destinationFile.absolutePath)
    } catch (e: Exception) {
        logger.error(e) { "Error saving image from URI: $uri" }
        Result.failure(e)
    }

    fun deleteImage(path: String): Result<Unit> = try {
        File(path).delete()
        Result.success(Unit)
    } catch (e: Exception) {
        logger.error(e) { "Error deleting image at path: $path" }
        Result.failure(e)
    }
}

fun getImageFile(path: String): File? {
    return try {
        File(path).takeIf { it.exists() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
