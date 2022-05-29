package com.xenatronics.cameraview.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material.icons.sharp.QrCode2
import androidx.compose.material.icons.sharp.TextFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xenatronics.cameraview.R
import com.xenatronics.cameraview.domain.PictureAction

@Composable
fun PictureControls(
    modifier: Modifier,
    onPictureAction: (PictureAction) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CameraControl(
            Icons.Sharp.ArrowBack,
            R.string.flip,
            modifier = Modifier.size(40.dp),
            onClick = { onPictureAction(PictureAction.BackAction) }
        )
        CameraControl(
            Icons.Sharp.TextFormat,
            R.string.recognition,
            modifier = Modifier
                .size(40.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape),
            onClick = { onPictureAction(PictureAction.TextRecognition) }
        )
        CameraControl(
            Icons.Sharp.QrCode2,
            R.string.codebar,
            modifier = Modifier
                .size(40.dp),
            onClick = { onPictureAction(PictureAction.CodeBarRecognition) }
        )
    }
}

