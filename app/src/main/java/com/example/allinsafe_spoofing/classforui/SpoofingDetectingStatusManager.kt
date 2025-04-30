package com.example.allinsafe_spoofing.classforui

import android.app.Application
import android.content.Context
import android.content.Intent
import com.example.allinsafe_spoofing.Ac5_02_spoofingdetect_process
import com.example.allinsafe_spoofing.Ac5_03_spoofoingdetect_completed

class SpoofingDetectingStatusManager(private val context: Context) {
    private var arpSpoofDetectResult=SpoofDetectResult("Arp",0,"","","")
    private var dnsSpoofDetectResult=SpoofDetectResult("Dns",0,"","","")
    //나중에 더 확장해야겠지만 지금은 이것만 넣겠습니다.
    fun arpSpoofingCompleted(severity: String){
        arpSpoofDetectResult.setType("Arp")
        arpSpoofDetectResult.setStatus(2)
        arpSpoofDetectResult.setSeverity(severity)
        checkIfAllCompleted()
    }
    fun dnsSpoofingCompleted(severity: String){
        dnsSpoofDetectResult.setType("Arp")
        dnsSpoofDetectResult.setStatus(2)
        dnsSpoofDetectResult.setSeverity(severity)
        checkIfAllCompleted()
    }
    private fun checkIfAllCompleted() {
        if (arpSpoofDetectResult.getStatus() == 2 && dnsSpoofDetectResult.getStatus() == 2) {
            // 두 탐지 모두 완료됨 -> 다음 액티비티로 이동
            val intent = Intent(context, Ac5_03_spoofoingdetect_completed::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // context가 Activity가 아닐 수 있으므로
            context.startActivity(intent)
        }
    }

}

class SpoofDetectResult( type:String, status:Int,severity: String, title: String, message: String){
    //status:
    //0 : 시작전
    //1 : 동작중
    //2 : 완료
    private var type=type
    private var status=status
    private var severity=severity
    private var title=title
    private var message=message

    fun init(type:String, status:Int,severity: String, title: String, message: String){init(type, status,severity, title, message)}

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
    fun setType(type:String){setType(type)} //왜 이걸 해야하는지는 모르겠음.
    fun setStatus(status:Int){setStatus(status)}
    fun setSeverity(severity: String){setSeverity(severity)}

    fun getStatus(): Int {return status}
}