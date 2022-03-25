package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class HoloSplashActivity : AppCompatActivity() {
    private val selectMain = "MainActivity"
    private val selectLogin = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holosplash)

        delaySec(1.5)   // 1.5초 지연
    }

    private fun delaySec(sec: Double){
        Handler(Looper.myLooper()!!).postDelayed({
            if (SettingInApp.mAuth.currentUser != null){
                val intentMain = Intent(this, MainActivity::class.java)
                SettingInApp.uniqueActivity(intentMain)
                startActivity(intentMain)
            }else{
                val intentLogin = Intent(this, LoginActivity::class.java)
                SettingInApp.uniqueActivity(intentLogin)
                startActivity(intentLogin)
            }
            finish()
        }, (sec*1000).toLong())
    }
}