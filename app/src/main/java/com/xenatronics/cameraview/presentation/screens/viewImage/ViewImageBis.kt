package com.xenatronics.cameraview.presentation.screens.viewImage

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import coil.compose.rememberImagePainter
import com.xenatronics.cameraview.common.util.getOutputDirectory
import com.xenatronics.cameraview.common.util.limitScales
import com.xenatronics.cameraview.common.util.valueLimits
import com.xenatronics.cameraview.domain.UIAction
import com.xenatronics.cameraview.domain.detectScanCode
import com.xenatronics.cameraview.domain.detectText
import com.xenatronics.cameraview.presentation.screens.components.ItemDetect
import com.xenatronics.cameraview.presentation.screens.components.PictureControls
import com.xenatronics.cameraview.presentation.screens.components.SlideImageControls


@Composable
fun ViewImageBis(
    photoUri: Uri,
    context: Context,
    onImageCaptured: (Uri) -> Unit,
    onBack: () -> Unit,
) {
    val top = 0f
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset(x = 0f, y = top)) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        rotation += rotationChange
        offset += offsetChange
    }
    scale = limitScales(scale)
    offset = valueLimits(scale, offset)

    val texte = remember { mutableStateOf("Scanner") }
    val scroll = rememberScrollState()

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImageCaptured(uri)
    }

    BoxWithConstraints {
        val contraints = constraintsLayout()
        ConstraintLayout(contraints) {
            Text(
                text = texte.value,
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(16.dp)
                    .verticalScroll(scroll),
            )
            Box(
                modifier = Modifier
                    //.align(Alignment.Center)
                    .layoutId("image")
                    .clipToBounds()
                    .fillMaxHeight(0.70f)
            ) {
                Image(
                    modifier = Modifier
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            rotationZ = 0.0f,
                            translationX = offset.x,
                            translationY = offset.y + top,
                            clip = false,
                            renderEffect = null
                        )
                        .pointerInput(this) {
                            detectTapGestures(
                                onLongPress = {
                                    scale = 1f
                                    offset = Offset.Zero
                                }
                            )
                        }
                        .transformable(state)
                        .fillMaxSize(),

                    painter = rememberImagePainter(photoUri),
                    contentDescription = null
                )
            }
            SlideImageControls(
                modifier =
                Modifier
                    .layoutId("options")
                    .background(color = Color.Black.copy(alpha = 0.45f)),
                list = listOf(
                    ItemDetect("Texte", UIAction.TextRecognition),
                    ItemDetect("Code Barre", UIAction.CodeBarRecognition),
                    ItemDetect("Encre", UIAction.IncRecognition),
                    ItemDetect("Traduction", UIAction.TranslateText)
                ),
                onAction = { action ->
                    when (action) {
                        is UIAction.TextRecognition -> {
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
                        is UIAction.CodeBarRecognition -> {
                            detectScanCode(
                                context = context,
                                photoUri = photoUri,
                                onResult = { value ->
                                    Toast.makeText(context, value, Toast.LENGTH_SHORT)
                                        .show()
                                },
                                onError = {
                                }
                            )
                        }
                        is UIAction.IncRecognition -> {
                        }
                        is UIAction.TranslateText -> {
                        }
                        is UIAction.BackAction -> {
                        }
                        else -> Unit
                    }
                })
            PictureControls(
                modifier = Modifier.layoutId("controls"),
                onPictureAction = { action ->
                    when (action) {
                        is UIAction.BackAction -> {
                            onBack()
                        }
                        is UIAction.RotationImage -> {

                        }
                        is UIAction.CodeBarRecognition -> {
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
                        is UIAction.MediaImage -> {
                            if (true == context.getOutputDirectory().listFiles()?.isNotEmpty()) {
                                galleryLauncher.launch("image/*")
                            }
                        }
                        is UIAction.TextRecognition -> {
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

fun constraintsLayout(): ConstraintSet {
    return ConstraintSet {
        val textValue = createRefFor("textValue")
        val image = createRefFor("image")
        val options = createRefFor("options")
        val controls = createRefFor("controls")

        constrain(textValue) {
            end.linkTo(parent.end)
            start.linkTo(parent.start)
            top.linkTo(parent.top, margin = 2.dp)
            bottom.linkTo(image.top, margin = 24.dp)
        }
        constrain(image) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(textValue.bottom)
            bottom.linkTo(controls.top)
        }
        constrain(options) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(image.bottom, margin = 1.dp)
            bottom.linkTo(controls.top)
        }
        constrain(controls) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(options.bottom, margin = 1.dp)
            bottom.linkTo(parent.bottom, margin = 0.dp)
        }
    }
}