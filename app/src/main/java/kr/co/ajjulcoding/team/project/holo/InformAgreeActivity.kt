package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityInformagreeBinding

class InformAgreeActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityInformagreeBinding
    private val binding get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInformagreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnConfirm.setOnClickListener { checkCbox() }
    }

    private fun checkCbox(){
        val arrCbox = arrayListOf(_binding.cboxAgree1)
        for (i in 0 until arrCbox.size){
            if (arrCbox[i].isChecked == false) {
                Toast.makeText(this, "모든 체크박스를 선택해주세요.", Toast.LENGTH_SHORT).show()
                break
            }
            else if (i == arrCbox.size-1){
                val intentCertifi = Intent(this, CertificationActivity::class.java)
                SettingInApp.uniqueActivity(intentCertifi)
                startActivity(intentCertifi)
            }
        }
    }
}