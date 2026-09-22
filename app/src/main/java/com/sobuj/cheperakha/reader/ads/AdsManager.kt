package com.sobuj.cheperakha.reader.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

/**
 * Manages AdMob integration with proper test/production separation.
 */
class AdsManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var isAdLoaded = false

    init {
        initializeAds()
    }

    /**
     * Initialize Mobile Ads SDK.
     */
    fun initializeAds(callback: ((Boolean) -> Unit)? = null) {
        MobileAds.initialize(context) { status: InitializationStatus ->
            val testDevices = listOf(
                RequestConfiguration.TEST_DEVICE_ID_EMULATOR
            )
            
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
            
            callback?.invoke(true)
        }
    }

    /**
     * Load an interstitial ad for natural transition points.
     */
    fun loadInterstitial(adUnitId: String) {
        val request = AdRequest.Builder().build()
        
        InterstitialAd.load(context, adUnitId, request, 
            object : com.google.android.gms.ads.InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoaded = false
                }
            })
    }

    /**
     * Show interstitial if available. Should only be called at natural breaks.
     */
    fun showInterstitial(onDismiss: (() -> Unit)? = null): Boolean {
        if (!isAdLoaded || interstitialAd == null) return false
        
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                isAdLoaded = false
                onDismiss?.invoke()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                isAdLoaded = false
                onDismiss?.invoke()
            }
        }

        interstitialAd?.show(context)
        return true
    }

    /**
     * Check if ads are enabled for this build.
     */
    fun isAdEnabled(): Boolean {
        return true // Controlled by AdMob App ID configuration
    }

    /**
     * Release resources.
     */
    fun destroy() {
        interstitialAd = null
    }
}
