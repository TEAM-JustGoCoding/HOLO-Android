package kr.co.ajjulcoding.team.project.holo

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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
    private lateinit var accountFragment:AccountFragment
    private lateinit var userSettingFragment:UsersettingFragment
    private val withdrawalDialogFragment = WithdrawalDialogFragment()
    private val utilityBillFragment = UtilityBillFragment()
    private lateinit var scoreFragment:ScoreFragment
    private lateinit var chatListFragment:ChatListFragment
    private lateinit var mUserInfo:HoloUser
    private val gpsFragment = GpsFragment()
    private var currentTag:String = HOME_TAG
    private lateinit var frgDic:HashMap<String, Fragment>
    private lateinit var dialog: DialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUserInfo = intent.getParcelableExtra<HoloUser>(AppTag.USER_INFO)!!
        profileFragment = ProfileFragment(mUserInfo)
        userSettingFragment = UsersettingFragment((mUserInfo))
        scoreFragment = ScoreFragment(mUserInfo)
        accountFragment = AccountFragment(mUserInfo)
        chatListFragment = ChatListFragment(mUserInfo)

        showHomeFragment(mUserInfo)
        frgDic = hashMapOf<String, Fragment>(AppTag.PROFILE_TAG to profileFragment,
            AppTag.GPS_TAG to gpsFragment, AppTag.SETTING_TAG to userSettingFragment,
            AppTag.WITHDRAWALDIALOG_TAG to withdrawalDialogFragment,
            AppTag.UTILITYBILLDIALOG_TAG to utilityBillFragment,
            AppTag.SCORE_TAG to scoreFragment, AppTag.ACCOUNT_TAG to accountFragment,
            AppTag.CHATLIST_TAG to chatListFragment)

        val code = intent.getStringExtra("first")
        if (code == "first") {
            dialog = UtilityBillFragment()
            dialog.show(supportFragmentManager, "CustomDialog")
        }

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
                R.id.menu_profile -> {
                    Log.d("프래그먼트 변경 요청", currentTag.toString())
                    changeFragment(AppTag.SETTING_TAG)
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
            else if(currentTag == AppTag.SCORE_TAG) {
                dialog = scoreFragment
                dialog.show(supportFragmentManager, "CustomDialog")
            }
            else if (currentTag == AppTag.GPS_TAG)
                frgDic[currentTag]!!.let { tran.add(R.id.fragmentView, it) }
            else {
                frgDic[currentTag]!!.let { tran.replace(R.id.fragmentView, it) }
            }
            tran.commit()
        }
    }

    fun changetoLoginActivity() {
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        editor.clear()
        editor.commit()
        val intent = Intent(this, LoginActivity::class.java)  // 인텐트를 생성해줌,
        startActivity(intent)  // 화면 전환을 시켜줌
        finish()
    }

    fun withdrawalUser() {
        val repository = Repository()

        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        editor.clear()
        editor.commit()

        val intentLogin = Intent(this, LoginActivity::class.java)
        SettingInApp.uniqueActivity(intentLogin)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intentLogin.flags =  // 탈퇴시 기존 스택 모두 비우고 로그인화면 생성"
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP //액티비티 스택제거
        CoroutineScope(Dispatchers.Main).launch {
            val result = repository.deleteUserInfo(AppTag.currentUserEmail()!!)

            Log.d("탈퇴 데이터 확인", result.toString())
            if (result != false) {
                intentLogin.putExtra(AppTag.currentUserEmail(), false)
                intentLogin.putExtra(AppTag.LOGIN_TAG, false)
                startActivity(intentLogin)
            }else Toast.makeText(this@MainActivity,"서버 통신 오류",Toast.LENGTH_SHORT).show()
        }

    }

    fun setLocationToHome(location:String){
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        userSettingFragment.setUserLocation(location)
        editor.putString("location",location).apply()   // save location
    }

    fun setAccount(account:String){
        sharedPref = this.getSharedPreferences(AppTag.USER_INFO,0)
        editor = sharedPref.edit()
        homeFragment.setUserAccount(account)
        editor.putString("account",account).apply()   // save account
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
        editor.putString("score", userInfo.score).apply()
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

    fun addAlarm(day: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, Alarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, Alarm.NOTIFICATION_ID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val toastMessage = if (true) {
            val cal = Calendar.getInstance()
            val year = (cal.get(Calendar.YEAR))

            for (i in year until year+10) {
                for (j in 0 until 12) {
                    setDateFormat(alarmManager, year, j, day, pendingIntent)
                }
            }

            "알림이 설정되었습니다."
        } else {
            alarmManager.cancel(pendingIntent)
            "알림이 설정되지 않았습니다."
        }
        Log.d(Alarm.TAG, toastMessage)
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()

    }

    private fun setDateFormat(
        alarmManager: AlarmManager,
        year: Int,
        month: Int,
        day: Int,
        pendingIntent: PendingIntent
    ) {

        val calendar: Calendar = Calendar.getInstance().apply { // 1
            timeInMillis = System.currentTimeMillis()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month-1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}