package kr.co.ajjulcoding.team.project.holo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        const val HOME_TAG = "HomeFragment"
    }
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var _binding:ActivityMainBinding
    private val binding get() = _binding
    private lateinit var homeFragment:HomeFragment
    private var currentTag:String = HOME_TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding.contraintMain.setOnClickListener { }
        showHomeFragment(intent.getSerializableExtra(AppTag.USER_INFO) as HoloUser)
        if (intent.getBooleanExtra(AppTag.LOGIN_TAG, false)) {
            saveCache()
        }
    }

    override fun onBackPressed() {
        // TODO("뒤로 가기 버튼 2번 연속 눌러야 종료 추가")
        super.onBackPressed()
    }

    private fun saveCache(){
        val userInfo = intent.getSerializableExtra(AppTag.USER_INFO) as HoloUser
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        editor.putString("uid", userInfo.uid).apply()
        editor.putString("realName",userInfo.realName).apply()
        editor.putString("nickName",userInfo.nickName).apply()
    }

    private fun showHomeFragment(userInfo:HoloUser){
        val tran = supportFragmentManager.beginTransaction()
        homeFragment = HomeFragment(userInfo)
        tran.add(R.id.fragmentView, homeFragment)
        tran.commit()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}