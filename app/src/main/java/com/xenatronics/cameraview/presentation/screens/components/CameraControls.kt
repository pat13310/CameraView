package com.xenatronics.cameraview.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.sharp.FlipCameraAndroid
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material.icons.sharp.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xenatronics.cameraview.R
import com.xenatronics.cameraview.domain.CameraUIAction

@Composable
fun CameraControls(
    available: Boolean,
    cameraUIAction: (CameraUIAction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        CameraControl(
            Icons.Sharp.FlipCameraAndroid,
            R.string.flip,
            modifier = Modifier.size(40.dp),
            onClick = { cameraUIAction(CameraUIAction.OnSwitchCameraClick) }
        )
        CameraControl(
            Icons.Sharp.Lens,
            R.string.camera,
            modifier = Modifier
                .size(64.dp)
                .padding(1.dp)
                .border(1.dp, Color.White, CircleShape),
            onClick = { cameraUIAction(CameraUIAction.OnCameraClick) }
        )


//
        if (available) {
            CameraControl(
                imageVector = Icons.Outlined.FlashOn,
                contentDescId = R.string.flash,
                modifier = Modifier.size(40.dp),
                onClick = { cameraUIAction(CameraUIAction.OnFlashCameraClick) }
            )
        } else {
            CameraControl(
                Icons.Sharp.PhotoLibrary,
                R.string.photos,
                modifier = Modifier.size(40.dp),
                onClick = { cameraUIAction(CameraUIAction.OnGalleryViewClick) }
            )
        }


    }
}
