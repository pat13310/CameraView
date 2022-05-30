package com.xenatronics.cameraview.presentation.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.xenatronics.cameraview.common.util.getOutputDirectory
import com.xenatronics.cameraview.common.util.limitScales
import com.xenatronics.cameraview.common.util.takePhoto
import com.xenatronics.cameraview.common.util.valueLimits
import com.xenatronics.cameraview.domain.CameraUIAction
import com.xenatronics.cameraview.domain.analyzer.FaceAnalyzer
import com.xenatronics.cameraview.domain.provider.getCameraProvider
import com.xenatronics.cameraview.presentation.screens.components.CameraControls
import java.util.concurrent.Executor

private var controlCamera: CameraControl? = null

@SuppressLint("RestrictedApi")
@Composable
fun ViewCapture(
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    onChangeLens: (Int) -> Unit,
    lensFacing: Int,
) {
    // 1
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    val faceAnalyzer = ImageAnalysis.Builder()
        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
        .build()
        .also {
            it.setAnalyzer(executor, FaceAnalyzer())
        }
    var camera: Camera?
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImageCaptured(uri)
    }
    var flashEnabled by rememberSaveable { mutableStateOf(false) }
    val flashAvailable = remember { mutableStateOf(true) }
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    // 2
    LaunchedEffect(key1 = lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.runCatching {
            unbindAll()
            bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture,
                faceAnalyzer
            )
        }.onSuccess {
            it.also { camera = it }
            controlCamera = camera?.cameraControl
            flashAvailable.value = preview.camera?.cameraInfo?.hasFlashUnit() ?: false
            if (flashAvailable.value) {
                controlCamera?.enableTorch(flashEnabled)
                //camera?.cameraControl?.enableTorch(flashEnabled)
            }
        }.onFailure {
            Log.d("OUPss", it.message.toString())
        }
        preview.setSurfaceProvider(executor, previewView.surfaceProvider)
    }
    scale = limitScales(scale)
    offset = valueLimits(scale, offset)
    // 3
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .fillMaxSize()

    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .fillMaxSize()
                .transformable(state)
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    rotationZ = 0.0f,
                    translationX = offset.x,
                    translationY = offset.y
                )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(text = offset.x.toString(), color = Color.White)
            CameraControls(flashAvailable.value) { action ->
                when (action) {
                    CameraUIAction.OnCameraClick -> {
                        imageCapture.takePhoto(
                            context,
                            lensFacing,
                            onImageCaptured = { uri, _ ->
                                onImageCaptured(uri)
                            }, onError
                        )
                    }
                    CameraUIAction.OnSwitchCameraClick -> {
                        onChangeLens(lensFacing)
                        if (lensFacing == CameraSelector.LENS_FACING_BACK)
                            flashEnabled = false
                    }
                    CameraUIAction.OnGalleryViewClick -> {
                        if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                            galleryLauncher.launch("image/*")
                        }
                    }
                    CameraUIAction.OnFlashCameraClick -> {
                        flashEnabled = !flashEnabled
                        controlCamera?.enableTorch(flashEnabled)
                    }
                    else -> Unit
                }
            }
        }
    }
}



private fun cameraInit(
    cameraSelector: CameraSelector,
    imageCapture: ImageCapture,
    faceAnalyzer: ImageAnalysis,
    preview: Preview,
    lifecycleOwner: LifecycleOwner,
    cameraProvider: ProcessCameraProvider
): Camera {
    var camera: Camera? = null
    try {
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
            faceAnalyzer
        )
    } catch (ex: Exception) {
        Log.d("err", ex.message.toString())
    }
    return camera!!
}