package com.xenatronics.cameraview

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xenatronics.cameraview.presentation.screens.ViewCapture
import com.xenatronics.cameraview.presentation.screens.ViewImage.ViewImage
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var photoUri: Uri

    //private val viewModel by viewModels<CameraViewModel>()
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)


    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        //Log.d("Main", "destroy")
        cameraExecutor.shutdown()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                shouldShowCamera.value = true
            } else {

            }
        }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

        }
        if (keyCode == KeyEvent.KEYCODE_POWER) {
        }

        return super.onKeyDown(keyCode, event)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val viewModel by viewModels<CameraViewModel>()
        setContent {
            if (shouldShowCamera.value) {
                //var lensFacing =  lensFacing
                var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

                ViewCapture(
                    lensFacing = lensFacing,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = {
                        Log.e("kilo", "View error:", it)
                    },
                    onChangeLens = {
                        lensFacing =
                            if (it == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else
                                CameraSelector.LENS_FACING_BACK
                    }
                )
            }
            if (shouldShowPhoto.value) {
                ViewImage(
                    text = photoUri.toString(),
                    photoUri = photoUri,
                    onClicked = {
                        shouldShowPhoto.value = false
                        shouldShowCamera.value = true
                    }
                )
            }
        }
        requestCameraPermission()
        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                shouldShowCamera.value = true
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this as Activity,
                Manifest.permission.CAMERA
            ) -> Unit
            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
