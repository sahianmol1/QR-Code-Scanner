package com.bestway.asqrscanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.bestway.asqrscanner.ui.presentation.qrcodescanner.QRCodeScannerScreen
import com.bestway.asqrscanner.ui.theme.ASQRScannerTheme
import com.google.mlkit.vision.barcode.BarcodeScanning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val barcodeScanner = BarcodeScanning.getClient()

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