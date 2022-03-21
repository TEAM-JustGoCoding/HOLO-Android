package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityInformagreeBinding

class InformAgreeActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityInformagreeBinding
    private val binding get() = _binding
    private val arrCbox = arrayListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityInformagreeBinding.inflate(layoutInflater)
        arrCbox.add(binding.cboxAgreeSmall1)
        arrCbox.add(binding.cboxAgreeSmall2)
        arrCbox.add(binding.cboxAgreeSmall3)
        setContentView(binding.root)

        arrCbox.forEach { checkBox ->
            checkBox.setOnClickListener {
                if (checkBox.isChecked == false){
                    binding.cboxAgree1.isChecked = false
                }
                val trueNum = arrCbox.filter { it.isChecked == true }
                if (trueNum.size == 3){
                    binding.cboxAgree1.isChecked = true
                }
            }
        }
        binding.cboxAgree1.setOnClickListener { toggleCboxSmall() }
        binding.btnConfirm.setOnClickListener { checkCbox() }
    }

    private fun checkCbox(){
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

    private fun toggleCboxSmall(){
        val vaild:Boolean = binding.cboxAgree1.isChecked

        if (vaild == false){
            binding.cboxAgreeSmall1.isChecked = false
            binding.cboxAgreeSmall2.isChecked = false
            binding.cboxAgreeSmall3.isChecked = false
        }else{
            binding.cboxAgreeSmall1.isChecked = true
            binding.cboxAgreeSmall2.isChecked = true
            binding.cboxAgreeSmall3.isChecked = true
        }
    }
}