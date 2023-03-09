package com.bestway.asqrscanner

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QRScannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
