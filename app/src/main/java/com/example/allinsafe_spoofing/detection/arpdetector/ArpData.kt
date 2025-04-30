package com.example.allinsafe_spoofing.detection.arpdetector

import java.net.InetAddress

data class ArpData(
    val senderIp: String,
    val senderMac: String,
    val targetIp: String,
    val targetMac: String
) {
    companion object {
        fun fromPacket(packet: ByteArray): ArpData? {
            return try {
                val senderIp = InetAddress.getByAddress(packet.copyOfRange(28, 32)).hostAddress
                val senderMac = packet.copyOfRange(22, 28).joinToString(":") { "%02X".format(it) }
                val targetIp = InetAddress.getByAddress(packet.copyOfRange(38, 42)).hostAddress
                val targetMac = packet.copyOfRange(32, 38).joinToString(":") { "%02X".format(it) }
                ArpData(senderIp, senderMac, targetIp, targetMac)
            } catch (e: Exception) {
                null
            }
        }
    }
    override fun toString(): String {
        return "ArpData(senderIp='$senderIp', senderMac='$senderMac', targetIp='$targetIp', targetMac='$targetMac')"
    }
}
