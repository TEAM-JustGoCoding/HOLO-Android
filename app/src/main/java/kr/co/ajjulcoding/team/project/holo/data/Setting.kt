package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class SettingInApp {
    companion object{
        val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
        val db = Firebase.firestore
        val SERVER_KEY = "AAAAPD-TwEc:APA91bG-D5WLN4mEJwAOoKhljjL92NIJcLfvGHuaUwH5gb4hh7X-saYh7pF-nTuTldRhd-U-5NIVOMHwrQeuQFbth-5uMASaX5ETGHCZ98UuzrHRrOYA-51rp1NjcwT1ao0w-6qoQKAI"
        val CONTENT_TYPE = "application/json"   //  데이터 json 타입으로 헤더에 넣음
        val FCM_BASE_URL = "https://fcm.googleapis.com"
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
        const val NOTIFICATION_TAG = "notificationTAG"

        const val SETTING_TAG = "settingTAG"
        const val WITHDRAWALDIALOG_TAG = "withdrawaldialogTAG"
        const val UTILITYBILLDIALOG_TAG = "utilitybilldialogTAG"
        const val SCORE_TAG = "scoreTAG"
        const val ACCOUNT_TAG = "accountTAG"
        const val BILLCACHE_TAG = "utilitybillcacheTAG"
        const val NOTIFICATIONCACHE_TAG = "notificationcacheTAG"
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
        const val URL_GET_ID:String = "getId.php"
        const val URL_GET_TOKEN:String = "getNicknameAndToken.php"
        const val URL_DELETE_USER:String = "withdrawal.php"
        const val URL_UPDATE_SCORE:String = "userScore.php"
        const val URL_UPDATE_COUNT:String = "userCount.php"
    }
}

class WebUrl {
    companion object{
        const val URL_BASE: String = "http://holo2.dothome.co.kr/"
        const val URL_DEAL: String = "?path=deliveryboard"
        const val URL_OTT: String = "?path=ottboard"
        const val URL_POLICY:String = "?path=policyboard"
        const val URL_DCM: String = "?path=documentboard"
        const val URL_FAQ: String = "?path=faqboard"
        const val URL_LIKE: String = "?path=likeboard"
        const val URL_SEARCH: String = "?path=allsearch&word="
    }
}