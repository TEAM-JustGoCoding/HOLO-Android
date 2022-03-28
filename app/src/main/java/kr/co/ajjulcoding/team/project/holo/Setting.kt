package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.lifecycle.LiveData
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
        const val REGISTER_TAG = "registerTAG"
        const val PROFILE_TAG = "profileTAG"
        const val GPS_TAG = "gpsTAG"
        fun currentUserEmail() = SettingInApp.mAuth.currentUser?.email
    }
}

class PhpUrl {
    companion object{
        const val DOTHOME:String = "http://holo.dothome.co.kr/"
        const val URL_REGISTER:String = "register.php"   // TODO("php 파일 연동")
        const val ULR_SELECT_USER:String = "login.php"
        const val URL_NICKNAME_DUPI:String = "nickNameDupli.php"
    }
}

// 캐시 관련
@Entity
data class UserCache(
    @PrimaryKey(autoGenerate = true) val id:Int,
    var uid: String,
    var nick_name:String,
    var real_name:String,
    var location:String?
)

@Dao
interface UserCacheDao{
    @Insert
    fun insertUser(userCaches: UserCache)

    @Query("UPDATE UserCache SET location =:location WHERE uid =:uid")
    fun updateLocation(uid:String,location:String)

    @Query("SELECT * FROM UserCache ORDER BY id DESC LIMIT 1")  // 최근 1건 저장된 유저 정보
    fun selectUser():LiveData<UserCache>

    @Delete
    fun deleteUser(userCaches: UserCache)
}

@Database(entities = [UserCache::class], version = 1)
abstract class UserCacheDatabase: RoomDatabase() {
    abstract val usercacheDao: UserCacheDao
}
