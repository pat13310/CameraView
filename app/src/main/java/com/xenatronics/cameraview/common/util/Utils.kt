package com.xenatronics.cameraview.common.util

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.ui.geometry.Offset
import androidx.core.net.toFile
import com.xenatronics.cameraview.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
private const val PHOTO_EXTENSION = ".jpg"


fun ImageCapture.takePhoto(
    context: Context,
    lensFacing: Int,
    onImageCaptured: (Uri, Boolean) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val outputDirectory = context.getOutputDirectory()
    // Create output file to hold the image
    val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
    val outputFileOption = getOutputFileOption(lensFacing, photoFile)

    this.takePicture(
        outputFileOption,
        Executors.newSingleThreadExecutor(),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: Uri.fromFile(photoFile)
                // If the folder selected is an external media directory, this is
                // unnecessary but otherwise other apps will not be able to access our
                // images unless we scan them using [MediaScannerConnection]
                val mimeType = MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(savedUri.toFile().extension)
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(savedUri.toFile().absolutePath),
                    arrayOf(mimeType)
                ) { _, uri ->

                }
                onImageCaptured(savedUri, false)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        })
}


fun Context.getOutputDirectory(): File {
    val mediaDir = this.externalMediaDirs.firstOrNull()?.let {
        File(it, this.resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else this.filesDir
}


fun getOutputFileOption(
    lensFacing: Int,
    photoFile: File
): ImageCapture.OutputFileOptions {

    // Setup image capture metadata
    val metadata = ImageCapture.Metadata().apply {
        // Mirror image when using the front camera
        isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
    }
    // Create output options object which contains file + metadata
    return ImageCapture.OutputFileOptions.Builder(photoFile)
        .setMetadata(metadata)
        .build()
}

fun createFile(baseFolder: File, format: String, extension: String) =
    File(
        baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension
    )


fun valueLimits(scale: Float, offset: Offset): Offset {
    if (scale < 1)
        return Offset(x = 0f, y = 18f)
    return offset
}

fun limitScales(scale: Float): Float {
    if (scale < 0.7)
        return 0.7f
    if (scale > 10)
        return 10f
    return scale
}

