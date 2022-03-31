package kr.co.ajjulcoding.team.project.holo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log

class HoloSplashActivity : AppCompatActivity() {
    private val selectMain = "MainActivity"
    private val selectLogin = "LoginActivity"
    private var userInfo: HoloUser? = null
    private var waitTime:Double = 1.5
    private lateinit var sharedPref:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holosplash)
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()

        if (SettingInApp.mAuth.currentUser != null) {
            userInfo = getUserCache()
            waitTime = 1.0
        }
        delaySec(waitTime, userInfo)   // 1.5 or 1.0 초 지연
    }

    private fun delaySec(sec: Double, userCache:HoloUser?){
        Handler(Looper.myLooper()!!).postDelayed({
            if (SettingInApp.mAuth.currentUser != null){
                val intentMain = Intent(this, MainActivity::class.java)
                SettingInApp.uniqueActivity(intentMain)
                intentMain.putExtra(AppTag.USER_INFO, userCache!!)
                startActivity(intentMain)
            }else{
                val intentLogin = Intent(this, LoginActivity::class.java)
                SettingInApp.uniqueActivity(intentLogin)
                startActivity(intentLogin)
            }
            finish()
        }, (sec*1000).toLong())
    }

    @SuppressLint("CommitPrefEdits")
    private fun getUserCache(): HoloUser{
        val result = HoloUser(
            sharedPref.getString("uid","아이디 없음")!!
            , sharedPref.getString("realName", "실명 없음")!!
            ,sharedPref.getString("nickName", "별명 없음")!!
            ,sharedPref.getString("location", null)
        )
        Log.d("사용자 정보 캐시 확인", result.toString())
        return result
    }
}