package com.xenatronics.cameraview.presentation.screens.viewImage

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.core.net.toFile

@Composable
fun ImageInfos(file: Uri) {
    val f = file.toFile()
    val totalSpace = f.length()
}