package com.xenatronics.cameraview.domain

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions


fun TranslatorToFrench(translateLanguage: String = TranslateLanguage.ENGLISH): Translator {
    val options = TranslatorOptions.Builder()
        .setSourceLanguage(translateLanguage)
        .setTargetLanguage(TranslateLanguage.FRENCH)
        .build()
    return Translation.getClient(options)
}