package kr.co.ajjulcoding.team.project.holo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.*
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityMainBinding
import java.io.File
import java.time.LocalDate
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
    private lateinit var chatListFragment:ChatListFragment
    private lateinit var mUserInfo:HoloUser
    private val gpsFragment = GpsFragment()
    private var currentTag:String = HOME_TAG
    private lateinit var frgDic:HashMap<String, Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserInfo = intent.getParcelableExtra<HoloUser>(AppTag.USER_INFO)!!
        profileFragment = ProfileFragment(mUserInfo)
        chatListFragment = ChatListFragment(mUserInfo)
        //binding.contraintMain.setOnClickListener { }
        showHomeFragment(mUserInfo)
        frgDic = hashMapOf<String, Fragment>(AppTag.PROFILE_TAG to profileFragment, AppTag.GPS_TAG to gpsFragment
        , AppTag.CHATLIST_TAG to chatListFragment)

        if (intent.getBooleanExtra(AppTag.LOGIN_TAG, false)) {
            CoroutineScope(Dispatchers.Main).launch {
                setProfileImgToHome("profile_" + mUserInfo.uid!!.replace(".", "") + ".jpg")
            }
            saveCache()
        }else if (intent.getBooleanExtra(AppTag.REGISTER_TAG, false)){
            saveCache()
        }

        binding.navigationBar.setOnItemSelectedListener { item ->
            Log.d("프래그먼트 변경 요청", currentTag.toString())
            when (item.itemId) {
                R.id.menu_home -> {
                    changeFragment(AppTag.HOME_TAG)
                }
                R.id.menu_chatting -> {
                    Log.d("프래그먼트 변경 요청", currentTag.toString())
                    changeFragment(AppTag.CHATLIST_TAG)
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        if (currentTag == AppTag.PROFILE_TAG ||
                currentTag == AppTag.GPS_TAG){
            changeFragment(AppTag.HOME_TAG)
        }else
            super.onBackPressed()
        // TODO("뒤로 가기 버튼 2번 연속 눌러야 종료 추가")
    }

    fun changeFragment(frgTAG: String){
        val tran = supportFragmentManager.beginTransaction()

        if (currentTag != frgTAG){
            currentTag = frgTAG
            if (AppTag.HOME_TAG == currentTag)
                tran.replace(R.id.fragmentView, homeFragment)   // (스택에 있는)이전 프래그먼트 전부 제거
            else {
                frgDic[currentTag]!!.let { tran.add(R.id.fragmentView, it) }
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
                Glide.with(this).load(imgUri).apply {
                    RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                }.into(findViewById(R.id.circleImageView))
                //Toast.makeText(this, "프로필 이미지 변경 완료!",Toast.LENGTH_SHORT).show()
                homeFragment.setUserProfile(imgUri.toString())
                sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)  // 캐시 저장
                editor = sharedPref.edit()
                editor.putString("profile",imgUri.toString()).apply()
            }

    }

    private fun saveCache(){
        val userInfo = intent.getParcelableExtra<HoloUser>(AppTag.USER_INFO) as HoloUser
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        editor.putString("uid", userInfo.uid).apply()
        editor.putString("realName",userInfo.realName).apply()
        editor.putString("nickName",userInfo.nickName).apply()
        editor.putString("token", userInfo.token).apply()
    }

    private fun showHomeFragment(userInfo:HoloUser){
        val tran = supportFragmentManager.beginTransaction()
        homeFragment = HomeFragment(userInfo)
        tran.replace(R.id.fragmentView, homeFragment)
        tran.commit()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun showAlertDialog(msg:String, vararg option:String){
        AlertDialog.Builder(this)
            .setTitle(msg)
            .setCancelable(false)
            .setItems(option, object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface, idx: Int) {
                    dialog.dismiss()
                }
            })
            .create().show()
    }
}