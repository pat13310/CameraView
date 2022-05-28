package com.xenatronics.cameraview.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions


private fun translatorBuilder(translateLanguage: String): Translator {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(translateLanguage)
        .setTargetLanguage(TranslateLanguage.FRENCH)
        .build()
    return Translation.getClient(options)
}


@Composable
fun DetectTranslator(
    text: String,
    translateLanguage: String = TranslateLanguage.ENGLISH,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
) {
    val downloaded = remember { mutableStateOf(false) }
    val translator = translatorBuilder(translateLanguage)
    val conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()
    if (!downloaded.value) {
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded successfully. Okay to start translating.
                // (Set a flag, unhide the translation UI, etc.)
                downloaded.value = true
            }
            .addOnFailureListener { exception ->
                onError(exception.message.toString())
            }
    }
    if (downloaded.value) {
        translator.translate(text)
            .addOnSuccessListener {
                onResult(it)
            }
            .addOnFailureListener {
                onError(it.message.toString())
            }
    }
}