package kr.co.ajjulcoding.team.project.holo

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HoloSplashActivity : AppCompatActivity() {
    private var userInfo: HoloUser? = null
    private var waitTime:Double = 1.5
    private lateinit var sharedPref:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    init {
        SettingInApp.db.firestoreSettings = SettingInApp.settings
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holosplash)
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        CoroutineScope(Dispatchers.Main).launch {
            if (SettingInApp.mAuth.currentUser != null) {
                if (checkNetwork()){
                    editor.putString("signature", System.currentTimeMillis().toString()).apply()
                }
                val repository = Repository() // 토큰 변경 여부 검사
                val token = repository.setToken(SettingInApp.mAuth.currentUser!!.email!!)
                userInfo = getUserCache(token)
                token?.let { editor.putString("token", it).apply() }
                waitTime = 1.0
                delaySec(waitTime, userInfo!!)   // 1.5 or 1.0 초 지연
            }else{
                delaySec(waitTime)
            }
        }
    }

    private fun delaySec(sec: Double, userCache:HoloUser?=null){
        Handler(Looper.myLooper()!!).postDelayed({
            if (SettingInApp.mAuth.currentUser != null){
                val intentMain = Intent(this, MainActivity::class.java)
                SettingInApp.uniqueActivity(intentMain)
                Log.d("로그인 당시 토큰",userCache!!.token.toString())
                intentMain.putExtra(AppTag.USER_INFO, userCache)
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
    private fun getUserCache(token:String?): HoloUser{
        var userId:Int? = sharedPref.getInt("id",-1)
        if (userId == -1)
            userId = null

        val result = HoloUser(
            userId
            ,sharedPref.getString("uid","아이디 없음")!!
            ,sharedPref.getString("realName", "실명 없음")!!
            ,sharedPref.getString("nickName", "별명 없음")!!
            ,sharedPref.getString("score", "평점 없음")!!
            ,sharedPref.getString("location", null)
            ,sharedPref.getString("profile", null)
            ,sharedPref.getString("account", null)
            ,sharedPref.getString("token", null)
            ,sharedPref.getBoolean("msgValid", true)
            ,Gson().fromJson(sharedPref.getString(AppTag.BILLCACHE_TAG, null), object : TypeToken<ArrayList<UtilityBillItem?>?>() {}.getType())
        )
        result.token = token ?: sharedPref.getString("token", null) // 인터넷 연결 없으면 토큰 캐시 정보 불러오기
        Log.d("사용자 정보 캐시 확인", result.toString())

        return result
    }

    private fun checkNetwork(): Boolean{
        val conManager = getSystemService(ConnectivityManager::class.java)
        val currentNet = conManager.activeNetwork ?: return false
        val actNet = conManager.getNetworkCapabilities(currentNet) ?: return false

        return when {
            actNet.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNet.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    }
}