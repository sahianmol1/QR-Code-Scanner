package com.bestway.asqrscanner.ui.presentation.qrcodescanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.bestway.asqrscanner.R
import com.bestway.asqrscanner.ui.presentation.QRCodeBackground
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException

@Composable
fun QRCodeScannerScreen(
    barcodeScanner: BarcodeScanner
) {
    var isTorchEnable by rememberSaveable { mutableStateOf(false) }
    var camera: Camera? by remember { mutableStateOf<Camera?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()
    var scannedResult by rememberSaveable { mutableStateOf("") }

    var selectedImageScannedResult by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showLoading by rememberSaveable { mutableStateOf(false) }
    var inputImage by remember { mutableStateOf<InputImage?>(null) }

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    var hasCameraPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri = uri
        }
    )

    LaunchedEffect(key1 = selectedImageUri) {
        selectedImageUri?.let { uri ->
            showLoading = true

            try {
                inputImage = InputImage.fromFilePath(context, uri)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            processImageProxy(
                barcodeScanner = barcodeScanner,
                inputImage = inputImage,
                onSuccess = { barcodes ->
                    showLoading = false

                    barcodes.forEach {
                        selectedImageScannedResult = it.rawValue.toString()
                    }
                }
            )
        }

        selectedImageUri = null
    }

    LaunchedEffect(key1 = selectedImageScannedResult) {
        if (selectedImageScannedResult.isNotEmpty() && selectedImageScannedResult.isNotBlank()) {
            Timber.d(selectedImageScannedResult)

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(selectedImageScannedResult)
            )
            context.startActivity(intent)
        }
        selectedImageScannedResult = ""
    }


    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(scannedResult) {
        if (scannedResult.isNotEmpty() && scannedResult.isNotBlank()) {
            Timber.d(scannedResult)

            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(scannedResult)
            )
            context.startActivity(intent)
        }
    }

    if (showLoading) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color.Black
            )
        }

    }

    if (hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    val previewView = PreviewView(context)
                    val preview = Preview.Builder().build()

                    val selector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(
                            Size(previewView.width, previewView.height)
                        )
                        .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalysis.setAnalyzer(
                        ContextCompat.getMainExecutor(context)
                    ) { imageProxy ->
                        coroutineScope.launch {
                            processImageProxy(
                                barcodeScanner = barcodeScanner,
                                imageProxy = imageProxy,
                                onSuccess = { barcodes ->
                                    scannedResult = ""

                                    barcodes.forEach {
                                        scannedResult = it.rawValue.toString()
                                    }
                                }
                            )
                        }
                    }

                    try {
                        camera = cameraProviderFuture.get().bindToLifecycle(
                            lifecycleOwner,
                            selector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            QRCodeBackground()

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {

                IconButton(
                    modifier = Modifier.padding(32.dp),
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_gallery),
                        contentDescription = stringResource(R.string.open_gallery),
                        tint = Color.White
                    )
                }

                IconButton(
                    modifier = Modifier.padding(32.dp),
                    onClick = {
                        isTorchEnable = !isTorchEnable
                        if (camera?.cameraInfo?.hasFlashUnit() == true) {
                            camera?.cameraControl?.enableTorch(isTorchEnable)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = if (isTorchEnable) R.drawable.ic_flash_off else R.drawable.ic_flash),
                        contentDescription = stringResource(R.string.turn_on_flash),
                        tint = Color.White
                    )
                }
            }
        }
    }
}