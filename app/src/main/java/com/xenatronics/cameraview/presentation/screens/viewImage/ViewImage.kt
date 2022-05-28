package com.xenatronics.cameraview.presentation.screens.viewImage

import android.content.Context
import android.net.Uri
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
import com.xenatronics.cameraview.domain.PictureAction
import com.xenatronics.cameraview.domain.detectScanCode
import com.xenatronics.cameraview.domain.detectText
import com.xenatronics.cameraview.presentation.screens.components.PictureControls

@Composable
fun ViewImage(
    photoUri: Uri,
    context: Context,
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
                        detectScanCode(
                            context = context,
                            photoUri = photoUri,
                            onResult = { value ->
                                Toast.makeText(context, "Valeur: $value", Toast.LENGTH_SHORT)
                                    .show()
                            },
                            onError = {
                            }
                        )
                    }
                    is PictureAction.TextRecognition -> {
                        detectText(
                            context = context,
                            photoUri = photoUri,
                            onResult = {
                                texte.value = it
                            },
                            onError = {
                            }
                        )
                    }
                    else -> Unit
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
