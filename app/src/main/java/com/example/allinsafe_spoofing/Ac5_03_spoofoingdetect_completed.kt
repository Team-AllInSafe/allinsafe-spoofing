package com.example.allinsafe_spoofing

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.allinsafe_spoofing.R
import com.example.allinsafe_spoofing.classforui.SpoofingDetectingStatusManager
import com.example.allinsafe_spoofing.databinding.Ac503SpoofingdetectCompletedBinding

class Ac5_03_spoofoingdetect_completed : ComponentActivity() {
    private lateinit var binding: Ac503SpoofingdetectCompletedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = Ac503SpoofingdetectCompletedBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        //ui확인을 위한 임시 부분임
//        set_arp_normal(binding)
//        set_dns_normal(binding)
//        var arp_isnormal=true
//        var dns_isnormal=true
//        binding.arpBg.setOnClickListener {
//            //ui확인을 위한 임시 함수로, 실제 ui적용시 삭제할 예정임
//            if(arp_isnormal){
//                arp_isnormal=false
//                set_arp_abnormal(binding)
//            }
//            else{
//                arp_isnormal=true
//                set_arp_normal(binding)
//            }
//
//        }
//        binding.dnsView.setOnClickListener {
//            //ui확인을 위한 임시 함수로, 실제 ui적용시 삭제할 예정임
//            if(dns_isnormal){
//                dns_isnormal=false
//                set_dns_abnormal(binding)
//            }
//            else{
//                dns_isnormal=true
//                set_dns_normal(binding)
//            }
//        }
//        //ui확인을 위한 임시 부분임

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.btnShowDetectHistory.setOnClickListener {
            var intent = Intent(this, Ac5_04_spoofingdetect_detect_history::class.java)
            startActivity(intent)
        }
        if (SpoofingDetectingStatusManager.getArpSeverity() == "CRITICAL"
            || SpoofingDetectingStatusManager.getArpSeverity()=="WARNING") {
            set_arp_abnormal(binding)
        } else {
            set_arp_normal(binding)
        }
        if (SpoofingDetectingStatusManager.getDnsSeverity() == "CRITICAL"
            || SpoofingDetectingStatusManager.getDnsSeverity() == "WARNING") {
            set_dns_abnormal(binding)
        }
        else{
            set_dns_normal(binding)
        }
    }
    fun set_arp_normal(binding: Ac503SpoofingdetectCompletedBinding){
        binding.arpBg.background = ContextCompat.getDrawable(binding.root.context, R.drawable.btn_round_green)
        binding.arpText.text="미탐지"
    }
    fun set_arp_abnormal(binding: Ac503SpoofingdetectCompletedBinding){
        binding.arpBg.background = ContextCompat.getDrawable(binding.root.context, R.drawable.btn_round_red)
        binding.arpText.text="탐지"
    }
    fun set_dns_normal(binding: Ac503SpoofingdetectCompletedBinding){
        binding.dnsView.background = ContextCompat.getDrawable(binding.root.context, R.drawable.btn_round_green)
        binding.dnsText.text="미탐지"
    }
    fun set_dns_abnormal(binding: Ac503SpoofingdetectCompletedBinding){
        binding.dnsView.background = ContextCompat.getDrawable(binding.root.context, R.drawable.btn_round_red)
        binding.dnsText.text="탐지"
    }
}