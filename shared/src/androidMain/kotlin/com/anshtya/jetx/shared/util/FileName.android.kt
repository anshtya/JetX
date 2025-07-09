package com.anshtya.jetx.shared.util

import java.io.File

actual fun getFileName(path: String) = File(path).name