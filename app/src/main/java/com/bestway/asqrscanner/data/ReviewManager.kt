package com.bestway.asqrscanner.data

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import timber.log.Timber

class ReviewManager(private val context: Context) {
    private val reviewManager = ReviewManagerFactory.create(context)

    fun requestReviewInfo() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(context as Activity, reviewInfo)
                flow.addOnCompleteListener { }
            } else {
                // There was some problem, log or handle the error code.
                Timber.e("In-App reviews: Something Went Wrong: ${task.exception}")
            }
        }
    }

}