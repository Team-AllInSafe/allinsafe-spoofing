package com.example.allinsafe_spoofing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.allinsafe_spoofing.databinding.Ac502SpoofingdetectProcessBinding
import com.example.allinsafe_spoofing.databinding.Ac506SpofingdetectItemLogBinding
import com.example.allinsafe_spoofing.detection.common.LogManager
import com.example.allinsafe_spoofing.detection.packettest.DummyPacketInjector
import java.util.logging.Logger

class Ac5_02_spoofingdetect_process : ComponentActivity() {
    private lateinit var binding: Ac502SpoofingdetectProcessBinding
//    private var isArpSpoofingCompleted=false
//    private var isDnsSpoofingCompleted=false
    companion object{
        // MainActivity 타입의 객체를 동반 객체로 선언한다(자바에서는 static)
        var activity502 : Ac5_02_spoofingdetect_process? = null
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity502=this
        binding=Ac502SpoofingdetectProcessBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val adapter=LogViewAdapter(listOf("로그1","로그2","로그3").toMutableList())
//        binding.recyclerLog.adapter=adapter

        val adapter =LogViewAdapter(LogManager.getLogs().toMutableList())
        binding.recyclerLog.adapter=adapter
        // ✅ 옵저버 등록해서 로그가 추가될 때마다 RecyclerView 갱신
        LogManager.addObserver { updatedLogs ->
            adapter.updateLogs(updatedLogs)
            binding.recyclerLog.scrollToPosition(adapter.itemCount - 1) // 마지막 로그로 스크롤
        }

        binding.backButton.setOnClickListener {
            finish()
        }
//        tempDetect_for10sec(binding){
//            detect_complete(binding)
//        }
        binding.progressbar.setProgress(1)
//      binding.textviewProcess.text="검사중..."

        binding.btnArpDummyPacket.setOnClickListener {
            LogManager.log("DEBUG", "ARP 더미 패킷 버튼 클릭됨")
            DummyPacketInjector.injectDummyArpData()
        }
        binding.btnDnsDummyPacket.setOnClickListener {
            DummyPacketInjector.injectDummyDnsPacket()
        }

    //텍스트뷰에 로그 받아오기 위한 코드(다신 쓸 일 없음)
//        while(true){
//            val logs = LogManager.getLogs()
//            val logText = logs.joinToString("\n")
//            binding.textviewProcess.text = logText
//            Thread.sleep(1000)
//        }
//    val logs = LogManager.getLogs()
//    val logText = logs.joinToString("\n")
//    //binding.textviewProcess.text = logText
//    Thread.sleep(1000)




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
class LogViewHolder(var binding: Ac506SpofingdetectItemLogBinding): RecyclerView.ViewHolder(binding.root)
class LogViewAdapter(private val LogList: MutableList<String>) :
RecyclerView.Adapter<LogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding=Ac506SpofingdetectItemLogBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return LogViewHolder(binding)
    }

    override fun getItemCount(): Int = LogList.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log=LogList[position]
        holder.binding.recyclerLog.text=log
    }
    fun updateLogs(newLogs: List<String>) {
        LogList.clear()
        LogList.addAll(newLogs)
        notifyDataSetChanged()
    }
}

//fun tempDetect_for10sec(binding: Ac502SpoofingdetectProcessBinding,onFinished: () -> Unit): Unit{
//    //프로그레스바에 대한 임시 함수로, 10초간 탐지(하는것처럼 보인) 후 다음 화면으로 넘어갑니다.
//    var i = 1
//    val handler = Handler(Looper.getMainLooper())
//    val runnable = object : Runnable {
//        override fun run() {
//            if (i <= 5) {
//                binding.progressbar.setProgress(10)
//                binding.textviewProcess.text = "${i}초"
//                i += 1
//                handler.postDelayed(this, 1000) // 1초 후 다시 실행
//            }
//            else{
//                onFinished()
//            }
//        }
//    }
//    handler.post(runnable) // 최초 실행
//}

