package com.mahimeta.sdk

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.mahimeta.sdk.utils.ManifestUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MahimetaAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private var adView: AdView? = null
    private var mahimetaAdSize: MahimetaAdSize = MahimetaAdSize.BANNER
    private var isAdLoading = false
    private val TAG = "MahimetaAdView"
    private val scope = CoroutineScope(Dispatchers.Main)

    init {
        setupAdView()
        setupAdListener()
        fetchAndLoadAd()
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Clean up resources when view is detached
        adView?.destroy()
        adView = null
    }

    /**
     * Sets the ad size for this ad view
     * @param adSize The new ad size to use
     */
    fun setAdSize(adSize: MahimetaAdSize) {
        if (mahimetaAdSize != adSize) {
            mahimetaAdSize = adSize
            setupAdView()
            fetchAndLoadAd()
        }
    }

/**
     * Loads an ad with the current ad unit ID
     */
    fun loadAd() {
        if (isAdLoading) {
            Log.w(TAG, "Ad is already loading")
            return
        }
        
        adView?.let { view ->
            isAdLoading = true
            Log.d(TAG, "Loading ad with unit ID: ${view.adUnitId}")
            view.loadAd(MahimetaSDK.createBannerAdRequest())
        } ?: run {
            Log.e(TAG, "AdView is not initialized")
        }
    }

    private fun setupAdView() {
        adView?.let {
            removeView(it)
            it.destroy()
        }

        adView = AdView(context).apply {
            // Set ad size first
            setAdSize(mahimetaAdSize.toGoogleAdSize(context))
            // Set layout params
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            Log.d(TAG, "Created AdView with ad size: $mahimetaAdSize")
        }
        addView(adView)
        setupAdListener()
    }
    private fun setupAdListener() {
        adView?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                isAdLoading = false
                Log.d(TAG, "Ad loaded successfully")
                // Optional: Add callback for ad loaded event
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
                isAdLoading = false
                Log.e(TAG, "Ad failed to load: $loadAdError")
                // Optional: Add callback for ad failed event

                // Consider implementing retry logic with exponential backoff
            }

            override fun onAdOpened() {
                super.onAdOpened()
                Log.d(TAG, "Ad opened")
            }

            override fun onAdClicked() {
                super.onAdClicked()
                Log.d(TAG, "Ad clicked")
            }

            override fun onAdClosed() {
                super.onAdClosed()
                Log.d(TAG, "Ad closed")
            }

            override fun onAdImpression() {
                super.onAdImpression()
                Log.d(TAG, "Ad impression recorded")
            }
        }
    }

    /**
     * Fetches the ad configuration and loads the ad
     */
    private fun fetchAndLoadAd() {
        scope.launch {
            try {
                val publisherId = ManifestUtils.getPublisherId(context)
                val response = MahimetaSDK.adConfigClient.getAdConfig(publisherId)
                
                if (response.success) {
                    // Set the ad unit ID and load the ad
                    adView?.adUnitId = response.data.adId
                    Log.d(TAG, "Using ad unit ID from API: ${response.data.adId}")
                    loadAd()
                } else {
                    Log.e(TAG, "Failed to fetch ad config")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching ad config: ${e.message}")
            }
        }
    }

    /**
     * Reloads the ad after the specified delay in milliseconds
     */
    fun reloadAd(delayMillis: Long = 30000) {
        postDelayed({ loadAd() }, delayMillis)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        adView?.resume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        adView?.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        adView?.destroy()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Auto-load ad when attached to window if not already loaded
        if (adView?.adListener == null) {
            setupAdListener()
        }
        // Don't call loadAd() here as it will be called after fetchAndLoadAd()
    }

    companion object {
        /**
         * Helper method to get the adaptive banner size for the current screen width
         */
        fun getAdaptiveBannerSize(context: Context): MahimetaAdSize {
            val displayMetrics = context.resources.displayMetrics
            val widthPixels = displayMetrics.widthPixels.toFloat()
            val density = displayMetrics.density
            val adWidth = (widthPixels / density).toInt()
            return MahimetaAdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
        }
    }
}