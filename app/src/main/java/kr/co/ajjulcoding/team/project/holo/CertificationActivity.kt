package kr.co.ajjulcoding.team.project.holo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityCertificationBinding
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityInformagreeBinding

class CertificationActivity : AppCompatActivity() {
    private var vaildSend:Boolean = true    // 연속으로 인증번호 전송 X
    private lateinit var _binding: ActivityCertificationBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCertificationBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.btnSendSMS.setOnClickListener {  }

    }

    private fun sendSMS(){

    }
}