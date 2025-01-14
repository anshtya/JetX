package com.anshtya.jetx.common.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.toBitmap

suspend fun Uri.toBitmap(context: Context): Bitmap {
    val loader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(this)
        .allowHardware(false)
        .build()

    val resultImage = (loader.execute(request) as SuccessResult).image
    return resultImage.toBitmap()
}