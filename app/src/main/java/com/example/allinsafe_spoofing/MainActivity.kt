package com.example.allinsafe_spoofing

import android.content.Intent
import android.net.VpnService
import android.os.*
import androidx.activity.ComponentActivity
import com.example.allinsafe_spoofing.classforui.MyApp
import com.example.allinsafe_spoofing.classforui.SpoofingDetectingStatusManager
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

        //dns, arp 더미 패킷을 삽입하기 위한 코드
        var insertArpDummyPacket = false
        var insertDnsDummyPacket = false

        //이거 켜두면 탐지 초기 화면 들어오자마자 vpn권한 요청할 수 있음.
        startVpnService()

        //dns, arp 탐지 과정을 위한 코드
        // val app = application as AppClass
        // val sdsManager = (application as SpoofingDetectingStatusManager).also {
        //     it.init(this) //이게 적용이 안 되는거같은데, 왠지 모르겠음
        // }

        detectionManager = SpoofingDetectionManager(
            arpDetector = arpDetector,
            dnsDetector = dnsDetector,
            alertManager = alertManager
        )

        val app = application as MyApp
        SpoofingDetectingStatusManager.init(this)
        binding = Ac501SpoofingdetectInitMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔙 뒤로가기 버튼
        binding.backButton.setOnClickListener {
            finish()
        }

        // ▶️ 탐지 시작 버튼
        binding.btnDetectStart.setOnClickListener {
            //탐지중 패킷 추가 위한 작업
            DummyPacketInjector.arp_init(detectionManager.arpDetector)
            DummyPacketInjector.dns_init(detectionManager.dnsDetector)

            //vpn 권한 요청
            startVpnService()

            //더미 패킷 삽입 (옵션)
            if (insertArpDummyPacket) {
                DummyPacketInjector.injectDummyArpData(detectionManager.arpDetector)
            }
            if (insertDnsDummyPacket) {
                DummyPacketInjector.injectDummyDnsPacket(detectionManager.dnsDetector)
            }

            if (SpoofingDetectingStatusManager.getIsCompletedPageStart() == false) {
                //더미 패킷을 통해 스푸핑을 감지하면 너무 결과창이 빨리 나와 진행중 페이지에 덮여버리는것을 막는 조건문입니다.
                val intent = Intent(this, Ac5_02_spoofingdetect_process::class.java)
                startActivity(intent)
                //finish() //필요한지 모르겠음
            }

            // ✅ 탐지 시작 (VPN 패킷을 가져와 분석함)
            detectionManager.startDetection {
                CustomVpnService.getLatestPacket()
            }

            // ✅ 5초 후 탐지 완료 화면으로 전환
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, Ac5_03_spoofingdetect_completed::class.java)
                startActivity(intent)
                finish()
            }, 5000)
        }

        // ✅ ARP 더미 패킷 삽입 예약
        binding.btnArpDummyPacket.setOnClickListener {
            //DummyPacketInjector.injectDummyArpData(detectionManager.arpDetector)
            insertArpDummyPacket = true
        }

        // ✅ DNS 더미 패킷 삽입 예약
        binding.btnDnsDummyPacket.setOnClickListener {
            //DummyPacketInjector.injectDummyDnsPacket(detectionManager.dnsDetector)
            insertDnsDummyPacket = true
        }

        // 📜 탐지 기록 보기
        binding.btnShowDetectHistory.setOnClickListener {
            val intent = Intent(this, Ac5_04_spoofingdetect_detect_history::class.java)
            startActivity(intent)
        }
    }

    // 🔐 VPN 권한 요청
    private fun startVpnService() {
        val intent = VpnService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, 1000)
        } else {
            onActivityResult(1000, RESULT_OK, null)
        }
    }

    // ✅ VPN 권한 획득 성공 시 실제 서비스 시작
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            val serviceIntent = Intent(this, CustomVpnService::class.java)
            startService(serviceIntent)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // 아래는 예전 UI용 컴포저블 주석 처리
//    @Composable
//    fun DetectionScreen(
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
