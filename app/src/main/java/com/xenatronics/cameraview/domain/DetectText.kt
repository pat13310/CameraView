package com.xenatronics.cameraview.domain

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions


fun detectText(
    context: Context,
    photoUri: Uri,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
) {
    val textRecognizer: TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    val image = InputImage.fromFilePath(context, photoUri)
    textRecognizer.process(image)
        .addOnSuccessListener {
            onResult(it.text)
        }
        .addOnFailureListener {
            onError(it.message.toString())
        }

}