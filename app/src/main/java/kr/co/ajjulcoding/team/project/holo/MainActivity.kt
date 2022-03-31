package kr.co.ajjulcoding.team.project.holo

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    companion object{
        const val HOME_TAG = "HomeFragment"
    }
    private lateinit var sharedPref: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var _binding:ActivityMainBinding
    private val binding get() = _binding
    private lateinit var homeFragment:HomeFragment
    private lateinit var profileFragment:ProfileFragment
    private lateinit var mUserInfo:HoloUser
    private val gpsFragment = GpsFragment()
    private var currentTag:String = HOME_TAG
    private lateinit var frgDic:HashMap<String, Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserInfo = intent.getSerializableExtra(AppTag.USER_INFO) as HoloUser
        profileFragment = ProfileFragment(mUserInfo)
        //binding.contraintMain.setOnClickListener { }
        showHomeFragment(mUserInfo)
        frgDic = hashMapOf<String, Fragment>(AppTag.PROFILE_TAG to profileFragment, AppTag.GPS_TAG to gpsFragment)

        if (intent.getBooleanExtra(AppTag.LOGIN_TAG, false)) {
            saveCache()
        }else if (intent.getBooleanExtra(AppTag.REGISTER_TAG, false)){
            saveCache()
        }
    }

    override fun onBackPressed() {
        // TODO("뒤로 가기 버튼 2번 연속 눌러야 종료 추가")
        super.onBackPressed()
    }

    fun changeFragment(frgTAG: String){
        val tran = supportFragmentManager.beginTransaction()

        if (currentTag != frgTAG){
            currentTag = frgTAG
            if (AppTag.HOME_TAG == currentTag)
                tran.replace(R.id.fragmentView, homeFragment)   // 이전 프래그먼트 제거
            else
                frgDic[currentTag]!!.let {tran.add(R.id.fragmentView, it)}  // 걍 위에 얹음
            tran.commit()
        }
    }

    fun setLocationToHome(location:String){
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        homeFragment.setUserLocation(location)
        editor.putString("location",location).apply()   // save location
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