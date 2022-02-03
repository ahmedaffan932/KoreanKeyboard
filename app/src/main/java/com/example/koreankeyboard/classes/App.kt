package com.example.koreankeyboard.classes

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

class App: Application() {

    private var appOpenManager: AppOpenManager? = null
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(
            this
        ) { initializationStatus: InitializationStatus? -> }
        appOpenManager = AppOpenManager(this)
    }
}