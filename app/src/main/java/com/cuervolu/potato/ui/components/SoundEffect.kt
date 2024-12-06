package com.cuervolu.potato.ui.components

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun SoundEffect(resourceId: Int?) {
    val context = LocalContext.current

    LaunchedEffect(resourceId) {
        resourceId?.let {
            val mediaPlayer = MediaPlayer.create(context, it)
            mediaPlayer.setOnCompletionListener { mp ->
                mp.release()
            }
            mediaPlayer.start()
        }
    }
}