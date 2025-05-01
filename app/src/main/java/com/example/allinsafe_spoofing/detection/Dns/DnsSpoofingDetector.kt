package com.example.allinsafe_spoofing.detection.dns

import android.util.Log
import com.example.allinsafe_spoofing.Ac5_02_spoofingdetect_process
import com.example.allinsafe_spoofing.classforui.SpoofingDetectingStatusManager
import com.example.allinsafe_spoofing.detection.common.AlertManager
import com.example.allinsafe_spoofing.detection.common.LogManager
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap

class DnsSpoofingDetector(
    private val alertManager: AlertManager
) {
    private val TAG = "DNS_DETECTOR"

    private val trustedDnsServers = setOf(
        "8.8.8.8", "8.8.4.4",
        "1.1.1.1",
        "9.9.9.9",
        "2001:4860:4860::8888", "2001:4860:4860::8844",
        "2606:4700:4700::1111", "2606:4700:4700::1001"
    )

    // 🔓 외부 접근 허용
    val pendingRequests = ConcurrentHashMap<Int, String>()

    private val warnedTxids = mutableSetOf<Int>()

    fun processPacket(buffer: ByteBuffer) {
        if (buffer.remaining() < 40) return

        val version = (buffer.get(0).toInt() shr 4) and 0xF
        val ipHeaderSize = if (version == 4) 20 else 40
        val udpHeaderSize = 8
        val dnsPayloadStart = ipHeaderSize + udpHeaderSize

        if (buffer.remaining() < dnsPayloadStart + 12) return

        val sourceIp = if (version == 4) {
            buffer.position(12)
            "${buffer.get().toInt() and 0xFF}.${buffer.get().toInt() and 0xFF}.${buffer.get().toInt() and 0xFF}.${buffer.get().toInt() and 0xFF}"
        } else {
            buffer.position(8)
            (0 until 8).joinToString(":") { String.format("%x", buffer.short) }
        }

        buffer.position(dnsPayloadStart)
        val txid = buffer.short.toInt() and 0xFFFF
        val flags = buffer.short.toInt() and 0xFFFF
        val isResponse = (flags and 0x8000) != 0

        if (!isResponse) {
            pendingRequests[txid] = sourceIp
            //Log.d(TAG, "[DNS Request Logged] TXID: $txid, 요청 서버: $sourceIp")
            LogManager.log(TAG, "[DNS Request Logged] TXID: $txid, 요청 서버: $sourceIp")
            return
        }

        var failedChecks = 0

        // ✅ 1. TXID 요청-응답 매칭 검사
        val expectedServer = pendingRequests[txid]
        if (expectedServer == null || expectedServer != sourceIp) {
            failedChecks++
        }

        // ✅ 2. TTL 값 검사
        val ttl = if (version == 4) {
            buffer.position(8)
            buffer.get().toInt() and 0xFF
        } else {
            buffer.position(7)
            buffer.get().toInt() and 0xFF
        }
        if (ttl < 10) {
            failedChecks++
        }

        // ✅ 3. 신뢰된 DNS 서버 검사
        if (sourceIp !in trustedDnsServers) {
            failedChecks++
        }

        // 중복 경고 방지
        if ((failedChecks == 2 || failedChecks == 3) && txid in warnedTxids) return
        if (failedChecks >= 2) warnedTxids.add(txid)

        logResult(sourceIp, txid, failedChecks)
    }

    private fun logResult(sourceIp: String, txid: Int, failedChecks: Int) {
        SpoofingDetectingStatusManager.dnsSpoofingCompleted("severity")
//        when (failedChecks) {
//            0, 1 -> Log.d(TAG, "[OK] 정상적인 DNS 응답 (출처: $sourceIp, TXID: $txid)")
//            2 -> {
//                Log.w(TAG, "[WARNING] DNS 스푸핑 의심 (출처: $sourceIp, TXID: $txid)")
//                alertManager.sendAlert("WARNING", "DNS 스푸핑 의심", "출처: $sourceIp, TXID: $txid")
//            }
//            3 -> {
//                Log.e(TAG, "[CRITICAL] 🚨🚨 DNS 스푸핑 감지 (출처: $sourceIp, TXID: $txid)")
//                alertManager.sendAlert("CRITICAL", "DNS 스푸핑 감지", "출처: $sourceIp, TXID: $txid")
//            }
//
//        }
        when (failedChecks) {
            0, 1 -> {LogManager.log(TAG, "[OK] 정상적인 DNS 응답 (출처: $sourceIp, TXID: $txid)")
                SpoofingDetectingStatusManager.dnsSpoofingCompleted("OK")}
            2 -> {
                LogManager.log(TAG, "[WARNING] DNS 스푸핑 의심 (출처: $sourceIp, TXID: $txid)")
                alertManager.sendAlert("WARNING", "DNS 스푸핑 의심", "출처: $sourceIp, TXID: $txid")
                SpoofingDetectingStatusManager.dnsSpoofingCompleted("WARNING")
            }
            3 -> {
                LogManager.log(TAG, "[CRITICAL] 🚨🚨 DNS 스푸핑 감지 (출처: $sourceIp, TXID: $txid)")
                alertManager.sendAlert("CRITICAL", "DNS 스푸핑 감지", "출처: $sourceIp, TXID: $txid")
                SpoofingDetectingStatusManager.dnsSpoofingCompleted("CRITICAL")
            }

        }
        //SpoofingDetectingStatusManager.dnsSpoofingCompleted("severity")
    }
}
