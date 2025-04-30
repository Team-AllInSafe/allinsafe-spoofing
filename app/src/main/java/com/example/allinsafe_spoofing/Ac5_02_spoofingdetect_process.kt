package com.example.allinsafe_spoofing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewbinding.ViewBinding
import com.example.allinsafe_spoofing.databinding.Ac502SpoofingdetectProcessBinding
import com.example.allinsafe_spoofing.detection.common.LogManager
import com.example.allinsafe_spoofing.detection.packettest.DummyPacketInjector
import java.util.logging.Logger

class Ac5_02_spoofingdetect_process : ComponentActivity() {
    private lateinit var binding: Ac502SpoofingdetectProcessBinding
//    private var isArpSpoofingCompleted=false
//    private var isDnsSpoofingCompleted=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=Ac502SpoofingdetectProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setOnClickListener {
            finish()
        }
//        tempDetect_for10sec(binding){
//            detect_complete(binding)
//        }
        binding.progressbar.setProgress(1)
        binding.textviewProcess.text="검사중..."
        binding.btnArpDummyPacket.setOnClickListener {
            DummyPacketInjector.injectDummyArpData()
        }
        binding.btnDnsDummyPacket.setOnClickListener {
            DummyPacketInjector.injectDummyDnsPacket()
        }
//        while(true){
//            val logs = LogManager.getLogs()
//            val logText = logs.joinToString("\n")
//            binding.textviewProcess.text = logText
//            Thread.sleep(1000)
//        }
    val logs = LogManager.getLogs()
    val logText = logs.joinToString("\n")
    binding.textviewProcess.text = logText
    Thread.sleep(1000)




}
//    fun updateActivityStatus(activityName: String) {
//        when (activityName) {
//            "Arp" -> isArpSpoofingCompleted = true
//            "Dns" -> isDnsSpoofingCompleted = true
//        }
//
//        // 두 액티비티가 모두 종료되었으면 다음 화면으로 이동
//        if (isArpSpoofingCompleted && isDnsSpoofingCompleted) {
//            startActivity(Intent(this, Ac5_03_spoofoingdetect_completed::class.java))
//            finish()  // 현재 액티비티 종료
//        }
//    }
    fun detect_complete(binding:Ac502SpoofingdetectProcessBinding){

        //실제 스푸핑 코드와 연동하였을때 사용하기 위한 함수
        //Toast.makeText(this.applicationContext,"스푸핑 탐지가 완료되었습니다!",Toast.LENGTH_LONG).show()
        var intent=Intent(this,Ac5_03_spoofoingdetect_completed::class.java)
        startActivity(intent)
    }
}

fun tempDetect_for10sec(binding: Ac502SpoofingdetectProcessBinding,onFinished: () -> Unit): Unit{
    //프로그레스바에 대한 임시 함수로, 10초간 탐지(하는것처럼 보인) 후 다음 화면으로 넘어갑니다.
    var i = 1
    val handler = Handler(Looper.getMainLooper())
    val runnable = object : Runnable {
        override fun run() {
            if (i <= 5) {
                binding.progressbar.setProgress(10)
                binding.textviewProcess.text = "${i}초"
                i += 1
                handler.postDelayed(this, 1000) // 1초 후 다시 실행
            }
            else{
                onFinished()
            }
        }
    }
    handler.post(runnable) // 최초 실행
}

