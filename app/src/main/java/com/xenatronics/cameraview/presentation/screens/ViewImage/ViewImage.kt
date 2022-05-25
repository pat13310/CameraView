package com.xenatronics.cameraview.presentation.screens.ViewImage

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ViewImage(
    photoUri: Uri,
    text: String,
    onClicked: () -> Unit
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


    androidx.compose.material.Surface {

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ImageInfos(file = photoUri)
            Text(text = text)
            Text(text = scale.toString())
            Image(
                painter = rememberImagePainter(photoUri),
                contentDescription = null,
                modifier = Modifier
                    .weight(4f)
                    .aspectRatio(0.66f)
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
        }
        Button(
            modifier = Modifier.height(40.dp),
            onClick = {
                onClicked()
            }
        ) {
            Text(text = "Retour")
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

