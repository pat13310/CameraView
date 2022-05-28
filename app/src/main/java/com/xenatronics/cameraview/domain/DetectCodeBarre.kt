package com.xenatronics.cameraview.domain

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


private fun codeBarBuilder(): BarcodeScanner {
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()
    return BarcodeScanning.getClient(options)
}

fun detectScanCode(
    context: Context,
    photoUri: Uri,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
) {
    val scanner = codeBarBuilder()
    val image = InputImage.fromFilePath(context, photoUri)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            for (barcode in barcodes) {
                onResult(barcode.rawValue!!)
            }
        }
        .addOnFailureListener {
            onError(it.message.toString())
        }
}