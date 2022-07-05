package kr.co.ajjulcoding.team.project.holo.view.activity

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.*
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.repository.Repository

class FinishSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finishsplash)

        val repository = Repository()
        val userInfo: HoloUser = intent.getParcelableExtra<HoloUser>("userInfo") as HoloUser

        CoroutineScope(Dispatchers.Main).launch {
            val result = repository.insertRegister(userInfo)
            val resultId:Int? = repository.getId(userInfo.uid)
            if (result != true && resultId == null){
                Toast.makeText(this@FinishSplashActivity, "서버와 통신에 실패했습니다!", Toast.LENGTH_SHORT).show()
                return@launch
            }
            userInfo.id = resultId
            val token:String? = repository.setToken(userInfo.uid!!)

            val fileName:String = userInfo.uid.replace(".","")+".jpg"
            val FBstorage = FirebaseStorage.getInstance()
            val FBstorageRef = FBstorage.reference
            val postRef = FBstorageRef.child("profile_img/profile_"+fileName)
            val drawableImg = R.drawable.background_profile
            val imgUri:Uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(drawableImg))
                .appendPath(resources.getResourceTypeName(drawableImg))
                .appendPath(resources.getResourceEntryName(drawableImg))
                .build()
            Log.d("uri 변환 확인", imgUri.toString())
            val uploadTask: UploadTask = postRef.putFile(imgUri)
            uploadTask.addOnSuccessListener {
                Log.d("최초 프로필 이미지 변경 완료", it.toString())
            }.addOnFailureListener{
                Log.d("최초 프로필 이미지 변경 오류", it.toString())
            }
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