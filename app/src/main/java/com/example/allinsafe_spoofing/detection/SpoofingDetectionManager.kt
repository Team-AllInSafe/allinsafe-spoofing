package com.example.allinsafe_spoofing.detection

import android.util.Log
import com.example.allinsafe_spoofing.detection.common.AlertManager
import com.example.allinsafe_spoofing.detection.dns.DnsSpoofingDetector
import com.example.allinsafe_spoofing.detection.arpdetector.ArpData
import com.example.allinsafe_spoofing.detection.arpdetector.ArpSpoofingDetector
import java.nio.ByteBuffer

class SpoofingDetectionManager(
    val arpDetector: ArpSpoofingDetector,
    val dnsDetector: DnsSpoofingDetector,
    private val alertManager: AlertManager
) {
    fun analyzePacket(packetData: ByteArray) {
        val buffer = ByteBuffer.wrap(packetData)

        if (isArpPacket(buffer)) {
            val arpData = ArpData.fromPacket(packetData)
            val isSpoofed = arpData != null && arpDetector.analyzePacket(arpData)
            if (isSpoofed && arpData != null) {
                alertManager.sendAlert(
                    severity = "CRITICAL",
                    title = "ARP 스푸핑 감지",
                    message = "IP: ${arpData.senderIp}, 변조된 MAC: ${arpData.senderMac}"
                )
            }
        }

        if (isDnsPacket(buffer)) {
            dnsDetector.processPacket(buffer)
        }
    }

    private fun isArpPacket(buffer: ByteBuffer): Boolean {
        return try {
            buffer.rewind()
            val ethTypeOffset = 12
            val etherType = buffer.getShort(ethTypeOffset).toInt() and 0xFFFF
            etherType == 0x0806
        } catch (e: Exception) {
            Log.e("SpoofingManager", "ARP 판별 실패: ${e.message}")
            false
        }
    }
    private fun isDnsPacket(buffer: ByteBuffer): Boolean {
        return try {
            buffer.rewind()
            val version = (buffer.get(0).toInt() shr 4) and 0xF
            val protocol = if (version == 4) buffer.get(9).toInt() and 0xFF
            else buffer.get(6).toInt() and 0xFF
            val srcPort = if (version == 4) buffer.getShort(20).toInt() and 0xFFFF
            else buffer.getShort(40).toInt() and 0xFFFF
            val dstPort = if (version == 4) buffer.getShort(22).toInt() and 0xFFFF
            else buffer.getShort(42).toInt() and 0xFFFF
            protocol == 17 && (srcPort == 53 || dstPort == 53)
        } catch (e: Exception) {
            Log.e("SpoofingManager", "DNS 판별 실패: ${e.message}")
            false
        }
    }
}
