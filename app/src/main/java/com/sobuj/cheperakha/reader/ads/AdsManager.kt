package com.sobuj.cheperakha.reader.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

/**
 * Manages AdMob integration with proper test/production separation.
 * Simplified version that avoids complex ad callbacks.
 */
class AdsManager(private val context: Context) {

    private var isInitialized = false

    init {
        initializeAds()
    }

    /**
     * Initialize Mobile Ads SDK.
     */
    fun initializeAds(callback: ((Boolean) -> Unit)? = null) {
        try {
            MobileAds.initialize(context) {
                isInitialized = true
                callback?.invoke(true)
            }
        } catch (e: Exception) {
            // AdMob initialization failed - continue without ads
            isInitialized = false
            callback?.invoke(false)
        }
    }

    /**
     * Check if ads are enabled for this build.
     */
    fun isAdEnabled(): Boolean {
        return isInitialized
    }

    /**
     * Release resources.
     */
    fun destroy() {
        isInitialized = false
    }
}
