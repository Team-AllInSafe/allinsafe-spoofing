package com.example.allinsafe_spoofing.classforui

import android.app.Application

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        SpoofingDetectingStatusManager.init(this)
    }
}