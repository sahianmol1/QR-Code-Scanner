package com.bestway.asqrscanner.ui.presentation.qrcodescanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@SuppressLint("UnsafeOptInUsageError")
suspend fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    imageProxy: ImageProxy,
    onSuccess: (barcodes: List<Barcode>) -> Unit,
) {
    val inputImage =
        imageProxy.image?.let { image ->
            InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        }

    try {
        inputImage?.let { image ->
            barcodeScanner.process(image).await()
        }?.let { barcodes ->
            onSuccess(barcodes)
        }
    } catch (exception: Exception) {
        Timber.e(exception.message.toString())
    } finally {
        imageProxy.close()
    }
}

suspend fun processImageProxy(
    barcodeScanner: BarcodeScanner,
    inputImage: InputImage?,
    onSuccess: (barcodes: List<Barcode>) -> Unit,
) {
    try {
        inputImage?.let { image ->
            barcodeScanner.process(image).await()
        }?.let { barcodes ->
            onSuccess(barcodes)
        }
    } catch (exception: Exception) {
        Timber.e(exception.message.toString())
    }
}

object ImageAnalyzer: ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image

        if(mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        }
    }

}