package com.bestway.asqrscanner

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bestway.asqrscanner.data.AppUpdateManager
import com.bestway.asqrscanner.data.ReviewManager
import com.bestway.asqrscanner.inappupdates.InAppUpdateManager
import com.bestway.asqrscanner.ui.presentation.qrcodescanner.QRCodeScannerScreen
import com.bestway.asqrscanner.ui.theme.ASQRScannerTheme
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var appUpdateListener: InstallStateUpdatedListener
    private val viewModel: MainViewModel by viewModels()
    private lateinit var inAppUpdate: InAppUpdateManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        viewModel.fetchUpdateTypeFromRemoteConfig()
        appUpdate()


        val barcodeScanner = BarcodeScanning.getClient(options)
        val reviewManager = ReviewManager(context = this)
        appUpdateManager = AppUpdateManager(this)

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


        // TODO: check if need to use the appUpdateInfo object to verify the install status
        appUpdateListener = InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                Timber.d("An update has been downloaded")

                // TODO: Show a snack bar
//                showSnackBarForCompleteUpdate()

                // TODO: complete update in the snackbar action click
                appUpdateManager.completeUpdate()
            }
        }

        appUpdateManager.registerListener(appUpdateListener)


        appUpdateManager.checkForUpdates()

        setContent {
            ASQRScannerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    QRCodeScannerScreen(
                        barcodeScanner = barcodeScanner,
                        onUpdateNowClick = { appUpdate() },
                        onDismissTextClick = { this@MainActivity.finish() }
                    )
                }
            }
        }


        // TODO: Use activity Contracts
//        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//            super.onActivityResult(requestCode, resultCode, data)
//
//            if (requestCode == APP_UPDATE_REQUEST_CODE) {
//                when (resultCode) {
//                    Activity.RESULT_OK -> {
//                        Timber.d("" + "Result Ok")
//                        //  handle user's approval }
//                    }
//                    Activity.RESULT_CANCELED -> {
//                        appUpdateManager.checkForUpdates()
//                        Timber.d("" + "Result Cancelled")
//                        //  handle user's rejection  }
//                    }
//                    ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
//                        appUpdateManager.checkForUpdates()
//                        Timber.d("" + "Update Failure")
//                    }
//                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        inAppUpdate.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        inAppUpdate.onDestroy()
    }

    private fun appUpdate() {
        lifecycleScope.launch {
            viewModel.appUpdateState.collect {
                inAppUpdate = InAppUpdateManager(
                    activity = this@MainActivity,
                    updateType = it.first,
                    updateVersion = it.second,
                    onFlexibleUpdateDownloaded = { flexibleUpdateDownloaded ->
                        viewModel.handleDownloadedFlexibleAppUpdate(isDownloaded = flexibleUpdateDownloaded)
                    },
                    onUpdateTriggered = {}
                )

                if (it.first == AppUpdateState.FORCE) {
                    viewModel.forceUpdateTriggered()
                }
            }
        }
    }
}