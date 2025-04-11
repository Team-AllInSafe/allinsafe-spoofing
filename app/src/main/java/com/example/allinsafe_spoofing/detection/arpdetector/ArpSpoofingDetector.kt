package com.example.allinsafe_spoofing.detection.arpdetector

import android.util.Log
import com.example.allinsafe_spoofing.detection.common.AlertManager
import java.io.File


class ArpSpoofingDetector(
    private val alertManager: AlertManager
) {
    companion object {
        private const val TAG = "ArpSpoofingDetector"
    }

    private val oldArpMap = mutableMapOf<String, String>()


    //ARP 테이블을 주기적으로 읽어 IP->MAC 변화 확인

    fun analyzePacket(arpData: ArpData): Boolean {
        val realMac = "00-50-56-f5-b8-cc" // 정상 MAC
        return if (arpData.senderMac != realMac) {
            Log.e(TAG, "🔥 [탐지됨] ${arpData.senderIp}: MAC 변조 감지 (${arpData.senderMac})")
            alertManager.sendAlert(
                severity = "CRITICAL",
                title = "ARP 스푸핑 감지",
                message = "IP: ${arpData.senderIp}, 기존 MAC: $realMac → 변조 MAC: ${arpData.senderMac}"
            )
            true
        } else {
            Log.d(TAG, "[정상] ARP 패킷: ${arpData.senderIp}")
            false
        }
    }


    fun checkArpTable() {

        val newArpMap = readArpFile()

        for ((ip, newMac) in newArpMap) {
            val oldMac = oldArpMap[ip]
            if (oldMac != null && oldMac != newMac) {
                Log.e(TAG, "[ARP SPOOFING DETECTED] $ip: $oldMac -> $newMac")
                alertManager.sendAlert(
                    severity = "CRITICAL",
                    title = "ARP 스푸핑 감지",
                    message = "IP=$ip, 기존MAC=$oldMac → 변조MAC=$newMac"
                )
            }
        }

        // oldArpMap 업데이트
        oldArpMap.clear()
        oldArpMap.putAll(newArpMap)

    }

    /**
     * /proc/net/arp 파일 읽어, IP->MAC 맵으로 반환
     */
    private fun readArpFile(): Map<String, String> {
        val arpMap = mutableMapOf<String, String>()
        try {
            File("/proc/net/arp").forEachLine { line ->
                val cols = line.split("\\s+".toRegex())
                if (cols.size >= 4 && cols[0] != "IP") {
                    val ip = cols[0]
                    val mac = cols[3]
                    if (mac.matches(Regex("..:..:..:..:..:.."))) {
                        arpMap[ip] = mac
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "접근 권한 오류: ${e.message}")
            alertManager.sendAlert(
                severity = "WARNING",
                title = "ARP 탐지 실패",
                message = "기기 보안 설정으로 인해 ARP 테이블 접근이 차단되었습니다."
            )
        }
        return arpMap
    }
}