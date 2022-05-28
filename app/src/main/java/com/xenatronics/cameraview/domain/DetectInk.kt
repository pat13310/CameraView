package com.xenatronics.cameraview.domain

import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.digitalink.*

private fun inkBuilder(language: String = "fr-Fr"): DigitalInkRecognizer? {
    // Specify the recognition model for a language
    var modelIdentifier: DigitalInkRecognitionModelIdentifier? = null
    try {
        modelIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag(language)
    } catch (e: MlKitException) {
        return null
    }
    if (modelIdentifier == null) {
        return null
        // no model was found, handle error.
    }
    val model: DigitalInkRecognitionModel =
        DigitalInkRecognitionModel.builder(modelIdentifier).build()
    // Get a recognizer for the language
    return DigitalInkRecognition.getClient(
        DigitalInkRecognizerOptions.builder(model).build()
    )
}

fun detectInk(
    ink: Ink,
    onResult: (String) -> Unit,
    onError: (String) -> Unit,
) {
    val scanner = inkBuilder()
    scanner?.recognize(ink)
        ?.addOnSuccessListener {
            onResult(it.candidates[0].text)
        }
        ?.addOnFailureListener {
            onError(it.message.toString())
        }
}