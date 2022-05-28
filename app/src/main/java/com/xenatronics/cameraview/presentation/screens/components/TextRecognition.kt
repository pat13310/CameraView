package com.xenatronics.cameraview.presentation.screens.components

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.util.concurrent.Executors


@Composable
fun TextRecognition() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val textReco = remember { mutableStateOf("") }
    val photoFile = File("")

    Column() {
        TextRecognitionView(
            context,
            lifecycleOwner,
            textReco,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            text = textReco.value,
        )
    }
}


@Composable
fun TextRecognitionView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    textReco: MutableState<String>,

    ) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val preview by remember { mutableStateOf(Preview.Builder().build()) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()

    val textRecognizer = remember {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .apply {
                            setAnalyzer(executor, DetectTextObject(textRecognizer, textReco))
                        }
                    val cameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()

                }, executor)
                preview.setSurfaceProvider(previewView.surfaceProvider)

                previewView
            }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }
    }
}


class DetectTextObject(
    private val textRecognizer: TextRecognizer,
    private var textReco: MutableState<String>
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        var mediaImage = imageProxy.image
        if (mediaImage != null) {
            val imageBuffer =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            textRecognizer.process(imageBuffer)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        textReco.value = it.result.text
                    }
                    imageProxy.close()
                }
        }
    }
}