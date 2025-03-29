package com.example.allinsafe_spoofing.detection.arpdetector

import android.util.Log

class ArpSpoofingDetector {
    fun analyzePacket(arpData: ArpData): Boolean {
        val realMac = getArpTable()[arpData.senderIp]
        return if (realMac != arpData.senderMac) {
            Log.e("SpoofingDetection", "ARP 스푸핑 감지! IP: ${arpData.senderIp}, 기존 MAC: $realMac → 변조된 MAC: ${arpData.senderMac}")
            true
        } else {
            false
        }
    }


    // 더미 데이터??
    private fun getArpTable(): Map<String, String> {
        return mapOf(
            "192.168.78.1" to "00-50-56-f5-b8-cc",
            "192.168.152.254" to "00-50-56-f2-ab-73"
        )
    }


    /* 실제 ARP 테이블 파싱 로직
    private fun getArpTable(): Map<String, String> {
        val arpTable = mutableMapOf<String, String>()
        try {
            File("/proc/net/arp").forEachLine { line ->
                val columns = line.split("\\s+".toRegex())
                if (columns.size >= 4 && columns[0] != "IP") {
                    arpTable[columns[0]] = columns[3]
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return arpTable
    }
    */
}
