package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLoginBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { checkLogin() }
        binding.btnSignin.setOnClickListener {
            val intentInfAg = Intent(this, InformAgreeActivity::class.java)
            SettingInActivity.uniqueActivity(intentInfAg)
            startActivity(intentInfAg)
        }
    }

    private fun checkLogin(){
        // TODO("로그인 유효성 확인")
    }
}