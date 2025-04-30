// ArpSpoofingDetector.kt
package com.example.allinsafe_spoofing.detection.arpdetector

import android.util.Log
import com.example.allinsafe_spoofing.detection.common.AlertManager

class ArpSpoofingDetector(
    private val alertManager: AlertManager
) {
    companion object {
        private const val TAG = "ArpSpoofingDetector"
    }

    // 여러 IP에 대해 정상 MAC 매핑
    private val knownMacTable = mapOf(
        "192.168.78.1" to "00-50-56-f5-b8-cc",
        "192.168.152.254" to "00-50-56-f2-ab-73"
    )

    fun analyzePacket(arpData: ArpData): Boolean {
        val expectedMac = knownMacTable[arpData.senderIp]

        return if (expectedMac != null && arpData.senderMac != expectedMac) {
            Log.e(TAG, "🔥 [탐지됨] ${arpData.senderIp}: 예상 MAC=$expectedMac, 수신 MAC=${arpData.senderMac}")
            alertManager.sendAlert(
                severity = "CRITICAL",
                title = "ARP 스푸핑 감지",
                message = "IP: ${arpData.senderIp}, 예상 MAC: $expectedMac → 변조 MAC: ${arpData.senderMac}"
            )
            true
        } else {
            Log.d(TAG, "[정상] ARP 패킷: ${arpData.senderIp} (${arpData.senderMac})")
            false
        }
    }
}
