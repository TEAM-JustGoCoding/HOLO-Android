package kr.co.ajjulcoding.team.project.holo

import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityMainBinding
import java.io.File
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
            else {
                frgDic[currentTag]!!.let { tran.replace(R.id.fragmentView, it) }
            }
            tran.commit()
        }
    }

    fun setLocationToHome(location:String){
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        homeFragment.setUserLocation(location)
        editor.putString("location",location).apply()   // save location
    }

    suspend fun setProfileImgToHome(fileName:String){
        val dir = File(Environment.DIRECTORY_PICTURES + "/profile_img")
        if (!dir.isDirectory()){
            dir.mkdir()    // 가져온 이미지 저장할 디렉토리 만들기
        }
        CoroutineScope(Dispatchers.IO).async {
            downloadImg(fileName)
        }.await()
    }

    private fun downloadImg(fileName: String){
        val FBstorage = FirebaseStorage.getInstance()
        val FBstorageRef = FBstorage.reference
        FBstorageRef.child("profile_img/"+fileName).downloadUrl
            .addOnSuccessListener { imgUri ->
                Log.d("저장한 프로필 url", imgUri.toString())
                Toast.makeText(this, "프로필 이미지 변경 완료!",Toast.LENGTH_SHORT).show()
                Glide.with(this).load(imgUri).into(findViewById(R.id.circleImageView))
            }

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