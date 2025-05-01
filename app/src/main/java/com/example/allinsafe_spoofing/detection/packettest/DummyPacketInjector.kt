package com.example.allinsafe_spoofing.detection.packettest

import android.util.Log
import com.example.allinsafe_spoofing.detection.arpdetector.ArpData
import com.example.allinsafe_spoofing.detection.arpdetector.ArpSpoofingDetector
import com.example.allinsafe_spoofing.detection.common.LogManager
import com.example.allinsafe_spoofing.detection.dns.DnsSpoofingDetector
import java.nio.ByteBuffer

object DummyPacketInjector {

    private const val TAG = "DummyPacketInjector"
    private lateinit var dns_detector:DnsSpoofingDetector
    private lateinit var arp_detector:ArpSpoofingDetector
    fun dns_init(detector: DnsSpoofingDetector){
        this.dns_detector=detector
    }
    fun arp_init(detector: ArpSpoofingDetector){
        this.arp_detector=detector
    }
    // 🔹 DNS 더미 패킷 삽입
    fun injectDummyDnsPacket(detector: DnsSpoofingDetector) {
        //Log.d(TAG, "🚀 테스트용 더미 DNS 패킷 삽입...")
        LogManager.log(TAG, "🚀 테스트용 더미 DNS 패킷 삽입...")
        detector.pendingRequests[0] = "8.8.8.8"  // 테스트용 요청 IP

        val dummyBuffer = ByteBuffer.allocate(60)

        // IPv4 Header
        dummyBuffer.put(0x45.toByte()) // Version + IHL
        dummyBuffer.put(0)             // DSCP + ECN
        dummyBuffer.putShort(60)       // Total Length
        dummyBuffer.putShort(0)        // Identification
        dummyBuffer.putShort(0)        // Flags + Fragment Offset
        dummyBuffer.put(64.toByte())   // TTL
        dummyBuffer.put(17.toByte())   // Protocol (UDP)
        dummyBuffer.putShort(0)        // Header checksum

        // Source IP: 192.168.1.100
        dummyBuffer.put(byteArrayOf(192.toByte(), 168.toByte(), 1.toByte(), 100.toByte()))
        // Destination IP: 10.0.0.1
        dummyBuffer.put(byteArrayOf(10.toByte(), 0.toByte(), 0.toByte(), 1.toByte()))

        // UDP Header
        dummyBuffer.putShort(53)       // Source Port
        dummyBuffer.putShort(5353)     // Destination Port
        dummyBuffer.putShort(20)       // Length
        dummyBuffer.putShort(0)        // Checksum

        // DNS Header (간단한 응답 형태)
        dummyBuffer.putShort(0.toShort())         // Transaction ID
        dummyBuffer.putShort(0x8180.toShort())    // Flags

        dummyBuffer.position(8)
        dummyBuffer.put(5.toByte()) // 임의 위치 조정 (포맷 유효하지 않을 수 있음 - 테스트 용도)

        dummyBuffer.rewind()
        detector.processPacket(dummyBuffer)
    }

    // 🔹 ARP 더미 패킷 삽입
    fun injectDummyArpData(detector: ArpSpoofingDetector) {
        //Log.d(TAG, "🧪 테스트용 더미 ARP 데이터 삽입...")
        LogManager.log(TAG, "🧪 테스트용 더미 ARP 데이터 삽입...")

        val dummyArp = ArpData(
            senderIp = "192.168.78.1",
            senderMac = "00-11-22-33-44-66",
            targetIp = "192.168.152.254",
            targetMac = "00-50-56-f5-b8-cc"
        )
        detector.analyzePacket(dummyArp)
    }

    //🔹 DNS 더미 패킷 삽입-detector없는 버전
    fun injectDummyDnsPacket() {
        //Log.d(TAG, "🚀 테스트용 더미 DNS 패킷 삽입...")
        LogManager.log(TAG, "🚀 테스트용 더미 DNS 패킷 삽입...")
        dns_detector.pendingRequests[0] = "8.8.8.8"  // 테스트용 요청 IP

        val dummyBuffer = ByteBuffer.allocate(60)

        // IPv4 Header
        dummyBuffer.put(0x45.toByte()) // Version + IHL
        dummyBuffer.put(0)             // DSCP + ECN
        dummyBuffer.putShort(60)       // Total Length
        dummyBuffer.putShort(0)        // Identification
        dummyBuffer.putShort(0)        // Flags + Fragment Offset
        dummyBuffer.put(64.toByte())   // TTL
        dummyBuffer.put(17.toByte())   // Protocol (UDP)
        dummyBuffer.putShort(0)        // Header checksum

        // Source IP: 192.168.1.100
        dummyBuffer.put(byteArrayOf(192.toByte(), 168.toByte(), 1.toByte(), 100.toByte()))
        // Destination IP: 10.0.0.1
        dummyBuffer.put(byteArrayOf(10.toByte(), 0.toByte(), 0.toByte(), 1.toByte()))

        // UDP Header
        dummyBuffer.putShort(53)       // Source Port
        dummyBuffer.putShort(5353)     // Destination Port
        dummyBuffer.putShort(20)       // Length
        dummyBuffer.putShort(0)        // Checksum

        // DNS Header (간단한 응답 형태)
        dummyBuffer.putShort(0.toShort())         // Transaction ID
        dummyBuffer.putShort(0x8180.toShort())    // Flags

        dummyBuffer.position(8)
        dummyBuffer.put(5.toByte()) // 임의 위치 조정 (포맷 유효하지 않을 수 있음 - 테스트 용도)

        dummyBuffer.rewind()
        dns_detector.processPacket(dummyBuffer)
    }
    // 🔹 ARP 더미 패킷 삽입-detector없는 버전
    fun injectDummyArpData() {
        //Log.d(TAG, "🧪 테스트용 더미 ARP 데이터 삽입...")
        LogManager.log(TAG, "🚀 테스트용 더미 ARP 패킷 삽입...")
        val dummyArp = ArpData(
            senderIp = "192.168.78.1",
            senderMac = "00-11-22-33-44-66",
            targetIp = "192.168.152.254",
            targetMac = "00-50-56-f5-b8-cc"
        )
        arp_detector.analyzePacket(dummyArp)
    }
}//

