package com.xenatronics.cameraview.domain

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


private fun gmsBarBuilder(context: Context): GmsBarcodeScanner {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()
    return GmsBarcodeScanning.getClient(context, options)
}

fun detectGmsCodeBarre(
    context: Context,
    onResult: (String) -> Unit,
    onError: (String) -> Unit
) {
    val scanner = gmsBarBuilder(context)
    scanner.startScan()
        .addOnSuccessListener { barcode ->
            onResult(barcode.rawValue.toString())
        }
        .addOnFailureListener { e ->
            onError(e.message.toString())
        }

}