package com.xenatronics.cameraview.presentation

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class CameraViewModel : ViewModel() {

    val scale by mutableStateOf("0f")
    val flashEnabled by mutableStateOf(false)
    var shouldShowPhoto by mutableStateOf(false)
    var shouldShowCamera by mutableStateOf(true)
    var photoUri: Uri? = null
    var lensFacing by mutableStateOf(CameraSelector.LENS_FACING_FRONT)

    fun handleImageCapture(uri: Uri) {
        //Log.i("kilo", "Image captured: $uri")
        shouldShowCamera = false
        photoUri = uri
        shouldShowPhoto = true
    }

    fun onChangeFacing(lensFacing: Int) {
        if (lensFacing == CameraSelector.LENS_FACING_BACK)
            this.lensFacing = CameraSelector.LENS_FACING_FRONT
        else
            this.lensFacing = CameraSelector.LENS_FACING_BACK
    }

}