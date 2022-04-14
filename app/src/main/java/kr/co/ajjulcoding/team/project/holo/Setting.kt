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
        const val RECEIVER_EMAIL = "remail"
        const val SENDER_EMAIL = "semail"
        const val LATEST_TIME = "latestTime"

        const val LOGIN_TAG = "loginTAG"
        const val REGISTER_TAG = "registerTAG"
        const val PROFILE_TAG = "profileTAG"
        const val GPS_TAG = "gpsTAG"
        const val HOME_TAG = "homeTAG"
        const val CHATLIST_TAG = "chatListTAG"
        const val CHATROOM_TAG = "chatRoomTAG"
        const val BUBBLELEFT_TAG = "bubbleLeftTAG"
        const val BUBBLERIGHT_TAG = "bubbleRightTAG"
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
        const val URL_GET_TOKEN:String = "getNicknameAndToken.php"
    }
}

//class ChatRoomDiffUtilCallBack(private val oldLi:ArrayList<ChatRoom>, private val newLi: ArrayList<ChatRoom>)
//    :DiffUtil.Callback(){
//    override fun getOldListSize(): Int = oldLi.size
//
//    override fun getNewListSize(): Int = newLi.size
//
//    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        val oldItem = oldLi[oldItemPosition]
//        val newItem = newLi[newItemPosition]
//
//        return if ((oldItem is ChatRoom) && (newItem is ChatRoom)){
//            Log.d("Diff 확인", oldItem.latestTime.toString()+"  "+newItem.latestTime.toString())
//            oldItem.latestTime == newItem.latestTime
//        }else
//            false
//    }
//
//    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
//        Log.d("Diff 확인", oldLi[oldItemPosition].toString()+"  "+newLi[newItemPosition])
//        return oldLi[oldItemPosition] == newLi[newItemPosition]
//    }
//
//}

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
