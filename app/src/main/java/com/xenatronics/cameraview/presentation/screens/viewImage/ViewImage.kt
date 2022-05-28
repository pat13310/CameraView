package com.xenatronics.cameraview.presentation.screens.viewImage

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.xenatronics.cameraview.domain.PictureAction
import com.xenatronics.cameraview.presentation.screens.components.PictureControls

@Composable
fun ViewImage(
    photoUri: Uri,
    context: Context,
    //text: String,
    //onClicked: () -> Unit,
    onBack: () -> Unit

) {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    scale = limitScales(scale)
    offset = valueLimits(scale, offset)

    val textRecognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(FORMAT_ALL_FORMATS)
        .build()

    val scanner = BarcodeScanning.getClient(options)

    val texte = remember { mutableStateOf("Scanner") }

    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = texte.value, modifier = Modifier.height(120.dp))
//            Text(text = scale.toString())
            Image(
                painter = rememberImagePainter(photoUri),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        rotationZ = 0.0f,
                        translationX = offset.x,
                        translationY = offset.y,
                        clip = false,
                        renderEffect = null
                    )
                    .transformable(state)
            )
            PictureControls(onPictureAction = { action ->
                when (action) {
                    is PictureAction.BackAction -> {
                        onBack()
                    }
                    is PictureAction.RotationImage -> {

                    }
                    is PictureAction.CodeBarRecognition -> {
                        val image = InputImage.fromFilePath(context, photoUri)
                        scanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    Toast.makeText(
                                        context, "Valeur: " + barcode.rawValue, Toast.LENGTH_SHORT
                                    )
                                        .show()
//                                    barcode.boundingBox?.let { rect ->
//                                        barcodeBoxView.setRect(
//                                            adjustBoundingRect(
//                                                rect
//                                            )
//                                        )
//                                    }
                                    if (barcode.format == FORMAT_EAN_13) {

                                    }
                                    if (barcode.format == FORMAT_QR_CODE) {

                                    }
                                    if (barcode.format == FORMAT_AZTEC) {

                                    }
                                }
                            }
                            .addOnFailureListener {

                            }

                    }
                    is PictureAction.TextRecognition -> {
                        val image = InputImage.fromFilePath(context, photoUri)
                        textRecognizer.process(image)
                            .addOnSuccessListener {
                                texte.value = it.text
                            }
                            .addOnFailureListener {
                                Log.d("Scanner erreur:", it.message.toString())
                            }
                    }
                }
            })
        }
    }
}

private fun valueLimits(scale: Float, offset: Offset): Offset {
    if (scale < 1)
        return Offset.Zero
    return offset
}

private fun limitScales(scale: Float): Float {
    if (scale < 0.7)
        return 0.7f
    if (scale > 10)
        return 10f
    return scale
}
