package com.example.koreankeyboard.classes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.koreankeyboard.BuildConfig
import com.example.koreankeyboard.R
import com.example.koreankeyboard.interfaces.InterstitialCallBack
import com.example.koreankeyboard.interfaces.LoadInterstitialCallBack
import com.example.koreankeyboard.interfaces.NativeAdCallBack
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

class Misc {
    companion object {

        const val suggestions = "suggestions"
        const val sound = "sounds"
        const val vibrate = "vibration"
        private const val keyboardSize = "keyboardSize"
        const val themeFromGallery: String = "themeFromGallery"
        const val data: String = "data"
        const val logKey = "logKey"
        private const val themeKey = "themeKey"
        private const val selectedKeyboard = "selectedKeyboard"
        private const val kyeBackground = "kyeBackground"
        const val appUrl: String =
            "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
        var intFailedCount = 0
        var mInterstitialAd: InterstitialAd? = null
        var mNativeAd: com.google.android.gms.ads.nativead.NativeAd? = null

        var nativeAdId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            "ca-app-pub-2344986107534073/5418008325"
        }
        var interstitialAdId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            "ca-app-pub-2344986107534073/9877618777"
        }


        var appOpenAddId = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/3419835294"
        } else {
            "ca-app-pub-2344986107534073~8756108792"
        }


        private var bannerAdId =
            if (BuildConfig.DEBUG)
                "ca-app-pub-3940256099942544/6300978111"
            else {
                "ca-app-pub-2344986107534073/3174988360"
            }

        fun getTheme(context: Context): Int {
            val sharedPreferences =
                context.getSharedPreferences(themeKey, Context.MODE_PRIVATE)
            return sharedPreferences.getInt(themeKey, 27)
        }


        fun zoomInView(view: View, activity: Activity, duration: Int) {
            val a: Animation =
                AnimationUtils.loadAnimation(activity, R.anim.zoom_in)
            a.duration = duration.toLong()
            view.startAnimation(a)
        }

        fun setTheme(context: Context) {
            val sharedPreferences =
                context.getSharedPreferences(themeKey, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            if (getTheme(context) > 20) {
                editor.putInt(themeKey, 0)
            } else {
                editor.putInt(themeKey, getTheme(context) + 1)
            }
            editor.apply()
        }

        fun setTheme(context: Context, themeId: Int) {
            val sharedPreferences =
                context.getSharedPreferences(themeKey, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt(themeKey, themeId)
            editor.apply()
        }

        fun setIskorean(context: Context, iskorean: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences(selectedKeyboard, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(selectedKeyboard, iskorean)
            editor.apply()
        }


        fun getIskorean(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(selectedKeyboard, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(selectedKeyboard, false)

        }

        fun isKeyBackgroundEnable(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(kyeBackground, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(kyeBackground, true)
        }

        fun setIsKeyBackgroundEnable(context: Context, isKeyBackground: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences(kyeBackground, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(kyeBackground, isKeyBackground)
            editor.apply()
        }

        @SuppressLint("MissingPermission")
        fun checkInternetConnection(context: Context): Boolean {
            //Check internet connection:
            val connectivityManager: ConnectivityManager? =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

            //Means that we are connected to a network (mobile or wi-fi)
            return connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!.state === NetworkInfo.State.CONNECTED ||
                    connectivityManager!!.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state === NetworkInfo.State.CONNECTED
        }


        fun initTopBar(activity: Activity, screenName: String) {
            activity.findViewById<TextView>(R.id.tvScreenName).text = screenName
            activity.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
                activity.onBackPressed()
            }
        }

        fun setIsSettingEnable(context: Context, isSettingEnabled: Boolean, settingName: String) {
            val sharedPreferences =
                context.getSharedPreferences(settingName, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(settingName, isSettingEnabled)
            editor.apply()
        }

        fun setIsKeyboardSizeLarge(context: Context, bool: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences(keyboardSize, AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(keyboardSize, bool)
            editor.apply()
        }

        fun getIsKeyboardSizeLarge(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(keyboardSize, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(keyboardSize, false)
        }

        fun getIsSettingEnable(context: Context, settingName: String): Boolean {
            val sharedPreferences =
                context.getSharedPreferences(settingName, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(settingName, true)
        }

        fun downloadTranslationModel(context: Context) {
            if (checkInternetConnection(context)) {

                val options = TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.KOREAN)
                    .build()
                val englishSpanishTranslator = Translation.getClient(options)

                englishSpanishTranslator.downloadModelIfNeeded()
                    .addOnSuccessListener {
                        setAToBDownloadStatus(context, true)
                        val optionsNew = TranslatorOptions.Builder()
                            .setSourceLanguage(TranslateLanguage.KOREAN)
                            .setTargetLanguage(TranslateLanguage.ENGLISH)
                            .build()
                        val spanishToEnglishTranslator = Translation.getClient(optionsNew)
                        spanishToEnglishTranslator.downloadModelIfNeeded()
                            .addOnSuccessListener {
                                setBToADownloadStatus(context, true)
                                Log.d(logKey, "spanishToEnglishTranslator")
                            }
                            .addOnFailureListener { exception ->
                                exception.printStackTrace()
                            }

                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }

            } else {
                if (!isAToBDownloaded(context) || !isBToADownloaded(context)) {
                    Toast.makeText(context, "Please check your internet.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }


        fun isBToADownloaded(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences("bToA", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("bToA", false)
        }

        fun isAToBDownloaded(context: Context): Boolean {
            val sharedPreferences =
                context.getSharedPreferences("aToB", Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("aToB", false)
        }

        fun setAToBDownloadStatus(context: Context, isDownloaded: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences("aToB", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("aToB", isDownloaded)
            editor.apply()
        }

        fun setBToADownloadStatus(context: Context, isDownloaded: Boolean) {
            val sharedPreferences =
                context.getSharedPreferences("bToA", AppCompatActivity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("bToA", isDownloaded)
            editor.apply()
        }


        fun loadInterstitial(
            activity: Activity
//            callback: LoadInterstitialCallBack?
        ) {
            if (intFailedCount < 3 && checkInternetConnection(activity)) {
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(
                    activity,
                    interstitialAdId,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e(logKey, adError.message)
                            Log.e(logKey, "Interstitial ad load failed.")
                            intFailedCount++
                            mInterstitialAd = null
//                            callback?.onFailed()
                        }

                        override fun onAdLoaded(p0: InterstitialAd) {
                            mInterstitialAd = p0
                            intFailedCount = 0
                            Log.d(logKey, "Interstitial Ad loaded.")
//                            callback?.onLoaded()
                        }
                    }
                )
            } else {
//                callback?.onFailed()
            }
        }


        fun showInterstitial(
            activity: Activity,
            callBack: InterstitialCallBack?
        ) {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(activity)
            } else {
                callBack?.onDismiss()
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
                loadInterstitial(activity)
                return
            }

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callBack?.onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    callBack?.onDismiss()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(logKey, "Ad showed fullscreen content.")
                    mInterstitialAd = null
                    loadInterstitial(activity)
                }
            }
        }


        fun loadNativeAd(activity: Activity, callBack: NativeAdCallBack?) {
            mNativeAd = null
            val adLoader: AdLoader =
                AdLoader.Builder(activity, nativeAdId)
                    .forNativeAd { nativeAd ->
                        Log.d(logKey, "Native Ad Loaded")


                        mNativeAd = nativeAd
                        if (activity.isDestroyed) {
                            nativeAd.destroy()
                        }
                        callBack?.onLoad()

                    }.withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e(logKey, adError.message)
                        }
                    })
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }


        fun showNativeAd(
            nativeAdTemplateView: TemplateView,
        ) {
            if (mNativeAd != null) {
                val styles =
                    NativeTemplateStyle.Builder()
                        .withMainBackgroundColor(ColorDrawable())
                        .build()
                nativeAdTemplateView.setStyles(styles)
                nativeAdTemplateView.setNativeAd(mNativeAd)

                if (mNativeAd?.mediaContent?.hasVideoContent() == true) {
                    mNativeAd?.mediaContent?.videoController?.play()
                }

                Log.d(logKey, "Native Ad displayed.")
            }
        }

        @SuppressLint("MissingPermission")
        fun loadBannerAd(activity: Activity, frameLayout: FrameLayout) {
            val bannerView = AdView(activity)
            bannerView.adUnitId = bannerAdId

            bannerView.adSize = getAdSize(activity, frameLayout)

            val adRequest = AdRequest.Builder().build()
            bannerView.loadAd(adRequest)
            bannerView.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    frameLayout.addView(bannerView)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                }

                override fun onAdOpened() {
                }

                override fun onAdClicked() {
                }

                override fun onAdClosed() {
                }
            }

        }


        private fun getAdSize(activity: Activity, frameLayout: FrameLayout): AdSize? {
            val display: Display = activity.windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val density = outMetrics.density
            var adWidthPixels = frameLayout.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }
    }
}