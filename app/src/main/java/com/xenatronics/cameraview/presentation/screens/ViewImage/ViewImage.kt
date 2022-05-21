package com.xenatronics.cameraview.presentation.screens.ViewImage

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
fun ViewImage(
    photoUri: Uri,
    text: String,
    onClicked: () -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageInfos(file = photoUri)
        Text(text = text)
        Image(
            painter = rememberImagePainter(photoUri),
            contentDescription = null,
            modifier = Modifier
                .weight(4f)
                .aspectRatio(0.66f)
        )
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