package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.room.*
import com.google.firebase.auth.FirebaseAuth

class SettingInApp {
    companion object{
        val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
        fun uniqueActivity(intent:Intent){
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}

class AppTag {
    companion object {
        const val USER_INFO = "user_info"

        const val LOGIN_TAG = "loginTAG"
        fun currentUserEmail() = SettingInApp.mAuth.currentUser?.email
    }
}

class PhpUrl {
    companion object{
        const val DOTHOME:String = "http://holo.dothome.co.kr/"
        const val URL_CREATE_REGISTER:String = "create_register.php"   // TODO("php 파일 연동")
        const val ULR_SELECT_USER:String = "login.php"
    }
}

// 캐시 관련
@Entity
data class UserCache(
    @PrimaryKey(autoGenerate = true) val id:Long,
    var uid: String,
    var nick_name:String,
    var real_name:String,
    var location:String
)

@Dao
interface UserCacheDao{
    @Insert
    fun insertUser(userCaches: UserCache)

    @androidx.room.Query("UPDATE UserCache SET location =:location WHERE uid =:uid")
    fun updateLocation(uid:String,location:String)

    @Delete
    fun deleteUser(userCaches: UserCache)
}
