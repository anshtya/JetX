package com.anshtya.jetx.shared.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap

object UriUtil {
    fun Uri.getMimeType(context: Context): String? {
        return if (this.scheme == ContentResolver.SCHEME_CONTENT) {
            context.contentResolver.getType(this)
        } else {
            val extension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase())
        }
    }
}