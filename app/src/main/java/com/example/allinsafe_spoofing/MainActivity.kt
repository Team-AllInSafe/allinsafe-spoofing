package com.example.allinsafe_spoofing

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.allinsafe_spoofing.databinding.Ac501SpoofingdetectInitMainBinding
import com.example.allinsafe_spoofing.detection.SpoofingDetectionManager
import com.example.allinsafe_spoofing.detection.arpdetector.ArpSpoofingDetector
import com.example.allinsafe_spoofing.detection.common.AlertManager
import com.example.allinsafe_spoofing.detection.dns.DnsSpoofingDetector
import com.example.allinsafe_spoofing.detection.packettest.DummyPacketInjector
import com.example.allinsafe_spoofing.vpn.CustomVpnService
import com.example.allinsafe_spoofing.ui.theme.AllinSafe_SpoofingTheme

class MainActivity : ComponentActivity() {
    private lateinit var binding: Ac501SpoofingdetectInitMainBinding
    private lateinit var detectionManager: SpoofingDetectionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val alertManager = AlertManager()
        val arpDetector = ArpSpoofingDetector(alertManager)
        val dnsDetector = DnsSpoofingDetector(alertManager)

        detectionManager = SpoofingDetectionManager(
            arpDetector = arpDetector,
            dnsDetector = dnsDetector,
            alertManager = alertManager
        )

//        setContent { 이전 ui 코드
//            AllinSafe_SpoofingTheme {
//                Surface(modifier = Modifier.fillMaxSize()) {
//                    DetectionScreen(
//                        onStartDetection = { startVpnService() },
//                        onInjectDns = {
//                            DummyPacketInjector.injectDummyDnsPacket(detectionManager.dnsDetector)
//                        },
//                        onInjectArp = {
//                            DummyPacketInjector.injectDummyArpData(detectionManager.arpDetector)
//                        }
//                    )
//                }
//            }
//        }
        binding=Ac501SpoofingdetectInitMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.btnDetectStart.setOnClickListener {
            startVpnService()
            var intent=Intent(this, Ac5_02_spoofingdetect_process::class.java)
            startActivity(intent)
        }
        binding.btnArpDummyPacket.setOnClickListener {
            DummyPacketInjector.injectDummyArpData(detectionManager.arpDetector)
        }
        binding.btnDnsDummyPacket.setOnClickListener {
            DummyPacketInjector.injectDummyDnsPacket(detectionManager.dnsDetector)
        }
        binding.btnShowDetectHistory.setOnClickListener {
            var intent = Intent(this,Ac5_04_spoofingdetect_detect_history::class.java)
            startActivity(intent)
        }
    }

    private fun startVpnService() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, 1000)
        } else {
            onActivityResult(1000, RESULT_OK, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            //여기가 스푸핑 탐지 완료한 부분인가?
            val serviceIntent = Intent(this, CustomVpnService::class.java)
            startService(serviceIntent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    @Composable
//    fun DetectionScreen( 이전 ui
//        onStartDetection: () -> Unit,
//        onInjectDns: () -> Unit,
//        onInjectArp: () -> Unit
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(32.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(text = "📡 스푸핑 탐지", style = MaterialTheme.typography.headlineMedium)
//            Spacer(modifier = Modifier.height(24.dp))
//            Button(onClick = onStartDetection) {
//                Text("탐지 시작")
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = onInjectDns) {
//                Text("🧪 DNS 더미 패킷 삽입")
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = onInjectArp) {
//                Text("🧪 ARP 더미 패킷 삽입")
//            }
//        }
//    }
}
