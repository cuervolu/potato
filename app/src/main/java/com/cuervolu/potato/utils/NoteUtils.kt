package com.cuervolu.potato.utils

import android.os.Build
import android.text.Html
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object NoteUtils {
    fun stripHtmlAndTruncate(htmlContent: String, maxLength: Int = 200): String {
        val plainText = Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT).toString().trim()

        if (plainText.length <= maxLength) return plainText

        return plainText.take(maxLength - 3) + "..."
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateDefaultTitle(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        return "Nota ${current.format(formatter)}"
    }
}