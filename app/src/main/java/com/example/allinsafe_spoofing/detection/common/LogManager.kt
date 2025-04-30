package com.example.allinsafe_spoofing.detection.common

import android.util.Log

object LogManager {
    private val logMessages = mutableListOf<String>()

    fun log(tag: String, message: String) {
        val formatted = "[$tag] $message"
        logMessages.add(formatted)
        Log.d(tag, message)
    }

    fun getLogs(): List<String> = logMessages
}
