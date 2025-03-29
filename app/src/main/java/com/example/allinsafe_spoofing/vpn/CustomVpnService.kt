package com.example.allinsafe_spoofing.vpn

import android.content.Intent
import android.net.VpnService
import android.os.*
import android.util.Log
import com.example.allinsafe_spoofing.detection.SpoofingDetectionManager
import com.example.allinsafe_spoofing.detection.common.AlertManager
import com.example.allinsafe_spoofing.detection.dns.DnsSpoofingDetector
import com.example.allinsafe_spoofing.detection.arpdetector.ArpSpoofingDetector
import java.io.FileInputStream
import java.nio.ByteBuffer

class CustomVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var packetCaptureThread: Thread? = null
    private var isCapturing = false

    private var detectionManager: SpoofingDetectionManager? = null
    private val buffer = ByteBuffer.allocate(32767)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("VPN", "VPN 서비스 시작 요청")
        startVpnSafely()
        return START_STICKY
    }

    private fun startVpnSafely() {
        try {
            stopVpn()

            val fd = Builder()
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")
                .establish()

            if (fd == null) {
                Log.e("VPN", "VPN 인터페이스 생성 실패")
                stopSelf()
                return
            }

            vpnInterface = fd
            Log.i("VPN", "VPN 인터페이스 설정 완료")

            val alertManager = AlertManager()
            val arpDetector = ArpSpoofingDetector()
            val dnsDetector = DnsSpoofingDetector(alertManager)

            detectionManager = SpoofingDetectionManager(
                arpDetector = arpDetector,
                dnsDetector = dnsDetector,
                alertManager = alertManager
            )

            Handler(Looper.getMainLooper()).postDelayed({
                if (vpnInterface != null) {
                    Log.i("VPN", "인터페이스 안정화 완료, 패킷 캡처 시작")
                    startPacketCapture()
                }
            }, 300)

        } catch (e: Exception) {
            Log.e("VPN", "VPN 시작 중 오류: ${e.message}")
            stopSelf()
        }
    }

    private fun startPacketCapture() {
        if (isCapturing) {
            Log.w("VPN", "이미 캡처 중")
            return
        }
        isCapturing = true

        packetCaptureThread = Thread {
            try {
                val fd = vpnInterface?.fileDescriptor ?: return@Thread
                val inputStream = FileInputStream(fd)
                Log.i("VPN", "패킷 캡처 스레드 시작")

                while (isCapturing) {
                    val length = inputStream.read(buffer.array())
                    if (length > 0) {
                        val packetData = buffer.array().copyOf(length)
                        detectionManager?.analyzePacket(packetData)
                    }
                }
            } catch (e: Exception) {
                Log.e("VPN", "캡처 중 오류: ${e.message}")
            }
        }
        packetCaptureThread?.start()
    }

    private fun stopPacketCapture() {
        isCapturing = false
        packetCaptureThread?.interrupt()
        packetCaptureThread = null
    }

    private fun stopVpn() {
        stopPacketCapture()
        vpnInterface?.close()
        vpnInterface = null
        Log.i("VPN", "VPN 인터페이스 종료")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        Log.i("VPN", "VPN 서비스 종료")
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
