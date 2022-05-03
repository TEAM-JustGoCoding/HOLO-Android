package kr.co.ajjulcoding.team.project.holo

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.firebase.ui.storage.images.FirebaseImageLoader
import java.io.InputStream

class SettingInApp {
    companion object{
        val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore

        fun uniqueActivity(intent:Intent){
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val settings = firestoreSettings {
            isPersistenceEnabled = true
            setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build()
        }
    }
}

class AppTag {
    companion object {
        const val USER_INFO = "user_info"
        const val PARTICIPANT = "participant"
        const val CHAT_TITLE = "title"
        const val CHAT_RANDOM = "randomDouble"
        const val LATEST_TIME = "latestTime"

        const val LOGIN_TAG = "loginTAG"
        const val REGISTER_TAG = "registerTAG"
        const val PROFILE_TAG = "profileTAG"
        const val GPS_TAG = "gpsTAG"
        const val HOME_TAG = "homeTAG"
        const val CHATLIST_TAG = "chatListTAG"
        const val CHATROOM_TAG = "chatRoomTAG"
        const val POSTSCORE_TAG = "postScoreTAG"

        const val SETTING_TAG = "settingTAG"
        const val WITHDRAWALDIALOG_TAG = "withdrawaldialogTAG"
        const val UTILITYBILLDIALOG_TAG = "utilitybilldialogTAG"
        const val SCORE_TAG = "scoreTAG"
        const val ACCOUNT_TAG = "accountTAG"
        fun currentUserEmail() = SettingInApp.mAuth.currentUser?.email
    }
}

class PhpUrl {
    companion object{
        const val DOTHOME:String = "http://holo.dothome.co.kr/"
        const val URL_REGISTER:String = "register.php"   // TODO("php 파일 연동")
        const val URL_SELECT_USER:String = "login.php"
        const val URL_NICKNAME_DUPI:String = "nickNameDupli.php"
        const val URL_POST_TOKEN:String = "postToken.php"
        const val URL_POST_SCORE:String = "postScore.php"
        const val URL_GET_TOKEN:String = "getNicknameAndToken.php"
        const val URL_DELETE_USER:String = "withdrawal.php"
        const val URL_UPDATE_SCORE:String = "userScore.php"
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
