package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinishSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finishsplash)

        val repository = Repository()
        val userInfo: HoloUser = intent.getParcelableExtra<HoloUser>("userInfo") as HoloUser

        CoroutineScope(Dispatchers.Main).launch {
            val result = repository.insertRegister(userInfo)
            if (result != true){
                Toast.makeText(this@FinishSplashActivity, "서버와 통신에 실패했습니다!", Toast.LENGTH_SHORT).show()
                return@launch
            }
            val token:String? = repository.setToken(userInfo.uid!!)
            Handler(Looper.myLooper()!!).postDelayed({
                token?.let { userInfo.token = it }
                val intentMain = Intent(this@FinishSplashActivity, MainActivity::class.java)
                SettingInApp.uniqueActivity(intentMain)
                intentMain.putExtra(AppTag.USER_INFO, userInfo)
                intentMain.putExtra(AppTag.REGISTER_TAG, true)
                intentMain.putExtra("first", "first")
                startActivity(intentMain)
                finish()
            }, (1*1000).toLong())
        }
    }
}