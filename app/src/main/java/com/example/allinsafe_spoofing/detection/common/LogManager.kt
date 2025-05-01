package com.example.allinsafe_spoofing.detection.common

import android.util.Log

object LogManager {
    private var logMessages = mutableListOf<String>()
    private val observers = mutableListOf<(List<String>) -> Unit>()
    fun log(tag: String, message: String) {
        val formatted = "[$tag] $message"
        logMessages.add(formatted)
        Log.d(tag, message)
        notifyObservers()
    }

    fun getLogs(): List<String> = logMessages
//    fun addLog(log: String) {
//        logMessages.add(log)
//        notifyObservers()
//    }

    fun addObserver(observer: (List<String>) -> Unit) {
        observers.add(observer)
        observer(logMessages) // 초기 로그 전달
    }

    private fun notifyObservers() {
        observers.forEach { it(logMessages) }
    }
}
