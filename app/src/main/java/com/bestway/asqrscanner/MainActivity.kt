package com.bestway.asqrscanner

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.bestway.asqrscanner.data.ReviewManager
import com.bestway.asqrscanner.ui.presentation.qrcodescanner.QRCodeScannerScreen
import com.bestway.asqrscanner.ui.theme.ASQRScannerTheme
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        val barcodeScanner = BarcodeScanning.getClient(options)
        val reviewManager = ReviewManager(context = this)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        var count = sharedPref.getInt("open_count", 0)

        with(sharedPref.edit()) {
            putInt("open_count", ++count)
            apply()
        }

        when (count % 3 == 0) {
            true -> {
                reviewManager.requestReviewInfo()
            }
            false -> {}
        }

        setContent {
            ASQRScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    QRCodeScannerScreen(
                        barcodeScanner = barcodeScanner
                    )
                }
            }
        }
    }
}