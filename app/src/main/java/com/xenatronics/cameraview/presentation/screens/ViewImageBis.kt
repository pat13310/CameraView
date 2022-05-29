package com.xenatronics.cameraview.presentation.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import coil.compose.rememberImagePainter
import com.xenatronics.cameraview.domain.PictureAction
import com.xenatronics.cameraview.domain.detectScanCode
import com.xenatronics.cameraview.domain.detectText
import com.xenatronics.cameraview.presentation.screens.components.ItemDetect
import com.xenatronics.cameraview.presentation.screens.components.PictureControls
import com.xenatronics.cameraview.presentation.screens.components.SlideImageControls


@Composable
fun ViewImageBis(
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
    //scale = limitScales(scale)
    //offset = valueLimits(scale, offset)

    val texte = remember { mutableStateOf("Scanner") }
    val scroll = rememberScrollState()

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

//            Box(
//                modifier = Modifier
//                    .layoutId("textValue")
//                    .background(Color.Black)
//                    //.verticalScroll(scroll)
//                    .fillMaxWidth()
//                    .requiredHeight(120.dp)
//                //.height(150.dp)
//                //.clip(MaterialTheme.shapes.medium)
//
//            ) {
//                var yOffset by remember { mutableStateOf(0f) }
//                Text(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .offset { IntOffset(0, yOffset.roundToInt()) }
//                        .draggable(orientation = Orientation.Vertical,
//                            state = rememberDraggableState { distance ->
//                                yOffset += distance
//                                if (yOffset > 0)
//                                    yOffset = 0f
//                                println(yOffset)
//                            }),
//                    //.padding(vertical = 15.dp)
//
//                    text = texte.value,
//                    color = MaterialTheme.colors.primary,
//                )
//
//            }
            Box(
                modifier = Modifier
                    .layoutId("image")

                    .clipToBounds()
            ) {

                Image(
                    modifier = Modifier
                        //.fillMaxWidth()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            rotationZ = 0.0f,
                            translationX = offset.x,
                            translationY = offset.y,
                            clip = false,
                            renderEffect = null
                        )
                        .transformable(state),

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
                    ItemDetect("Texte", PictureAction.TextRecognition),
                    ItemDetect("Code Barre", PictureAction.CodeBarRecognition),
                    ItemDetect("Encre", PictureAction.IncRecognition),
                    ItemDetect("Traduction", PictureAction.TranslateText)
                ),
                onAction = { action ->
                    when (action) {
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
                        is PictureAction.CodeBarRecognition -> {
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
                        is PictureAction.IncRecognition -> {

                        }
                        is PictureAction.TranslateText -> {

                        }
                        is PictureAction.BackAction -> {

                        }
                        else -> Unit
                    }
                })
            PictureControls(
                modifier = Modifier.layoutId("controls"),
                onPictureAction = { action ->
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

fun constraintsLayout(): ConstraintSet {
    return ConstraintSet {
        val textValue = createRefFor("textValue")
        val image = createRefFor("image")
        val options = createRefFor("options")
        val controls = createRefFor("controls")

        constrain(textValue) {
            top.linkTo(parent.top)
            bottom.linkTo(image.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(image) {
            top.linkTo(textValue.bottom)
            bottom.linkTo(controls.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(options) {
            top.linkTo(image.bottom)
            bottom.linkTo(controls.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(controls) {
            top.linkTo(options.bottom)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }
}