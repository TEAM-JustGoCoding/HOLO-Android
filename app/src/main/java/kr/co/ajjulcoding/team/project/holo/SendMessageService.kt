package kr.co.ajjulcoding.team.project.holo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.lang.reflect.Type

class SendMessageService: FirebaseMessagingService() {
    companion object{
        const val HOME_TYPE = "home"
        const val CHAT_TYPE = "chatting"
        const val CHAT_LIST_TYPE = "chatList"
        const val CMT_TYPE = "comment"
    }
    private var type: String = CMT_TYPE
    private var remoteMSG: RemoteMessage? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {  // 알림 데이터 수신
        super.onMessageReceived(remoteMessage)

        val sharedPref: SharedPreferences = this.getSharedPreferences(AppTag.USER_INFO, 0)
        val msgVaild: Boolean = sharedPref.getBoolean("msgValid", true)     // 알람 송수신 캐시 체크
        Log.d("서비스단 푸시 알림 수신", msgVaild.toString())
        if (msgVaild == false)
            return

        val title = "Holo"
        val msg = remoteMessage.data["msg"]!! // ex. 댓글이 달렸습니다.
        val content = remoteMessage.data["content"]!! // ex. 8000원 정도 주문할 예정입니다.
        remoteMSG = remoteMessage

        (remoteMessage.data["chatData"])?.let { it ->
            val jsonData = JSONObject(it)

            val currentNum: Double? = ChatRoomActivity.randomNum
            type = CHAT_TYPE
            when (currentNum) {  // 현재 접속한 채팅방과 알림 온 방이 동일 체크
                jsonData.getDouble("randomDouble") -> return
                else -> { }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            sendNotificationInP(type, title, msg, content)
        else
            sendNotfication(title, remoteMessage.notification?.body!!)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun sendNotificationInP(type: String, title: String, msg: String, content: String){
        var pendingIntent:PendingIntent? = null
        val intentMove = Intent(this, HoloSplashActivity::class.java)

        if (type == CMT_TYPE){  // 댓글/답글 => 해당 웹페이지 & 채팅방 생성 => 채팅 리스트
            intentMove.putExtra(CMT_TYPE, remoteMSG!!.data["url"])   // url: String(url, CHAT_LIST_TYPE)
            SettingInApp.uniqueActivity(intentMove)
            pendingIntent = PendingIntent.getActivity(this, 0, intentMove,
                PendingIntent.FLAG_IMMUTABLE)

            //알림 구현 틀
            val sharedPref: SharedPreferences = this.getSharedPreferences(AppTag.USER_INFO, 0)
            Log.d("string 확인", sharedPref.getString(AppTag.NOTIFICATIONCACHE_TAG, null).toString())

            val type: Type = object : TypeToken<ArrayList<NotificationItem?>?>() {}.getType()
            val gson = Gson()
            val json = sharedPref.getString(AppTag.NOTIFICATIONCACHE_TAG, "")
            var mNotificationItems = gson.fromJson(json, type) as ArrayList<NotificationItem?>?

//            var mNotificationItems = Gson().fromJson(sharedPref.getString(AppTag.NOTIFICATIONCACHE_TAG, null), object : TypeToken<ArrayList<NotificationItem?>?>() {}.getType())
//            var mNotificationItems = sharedPref.getString(AppTag.NOTIFICATIONCACHE_TAG, null)
//            var mNotificationItems = ArrayList<NotificationItem>()
            if (mNotificationItems==null)
                mNotificationItems= ArrayList()
            mNotificationItems!!.add(0, NotificationItem(msg, content, WebUrl.URL_LAN + remoteMSG!!.data["url"]))
            storeNotificationCache(mNotificationItems)
        }
        else{   // 채팅 => 채팅방
            var chatData: SimpleChatRoom? = null
            remoteMSG!!.data["chatData"]?.let {
                val jsonData = JSONObject(it)
                chatData = SimpleChatRoom(
                    jsonData.getString("title"), ArrayList()
                    , jsonData.getDouble("randomDouble"), jsonData.getString("semail")
                    , jsonData.getString("snickName"), jsonData.getString("stoken")
                    , jsonData.getString("remail"), jsonData.getString("rnickName")
                    , jsonData.getString("rtoken")
                )
            }
            intentMove.putExtra(CHAT_TYPE, chatData) // random: String(Double로 변환 필요)
            SettingInApp.uniqueActivity(intentMove)
            pendingIntent = PendingIntent.getActivity(this, 0, intentMove,
                PendingIntent.FLAG_IMMUTABLE)
        }

        val main: Person = Person.Builder()
            .setName(msg)
            .setIcon(IconCompat.createWithResource(this, R.drawable.app_icon))
            .build()
        val compatContent = NotificationCompat.MessagingStyle.Message(
            content,
            System.currentTimeMillis(),
            main
        )
        val messageStyle = NotificationCompat.MessagingStyle(main).addMessage(compatContent)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "service")
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(messageStyle)
            .setSmallIcon(R.drawable.icon_small)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8(Oreo) 버전 예외처리
            val channel = NotificationChannel("service","알림 메시지",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendNotfication(title: String, msg: String){
        val intentMove = Intent(this, HoloSplashActivity::class.java)
        // TODO: 채팅, 댓글/답글 경우에 따라서 나누기
        SettingInApp.uniqueActivity(intentMove)
        val pendingIntent:PendingIntent = PendingIntent.getActivity(this, 0, intentMove,
            PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this,"service")
            .setContentTitle(title)
            .setContentText(msg)
            .setSmallIcon(R.drawable.icon_small)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8(Oreo) 버전 예외처리
            val channel = NotificationChannel("service","알림 메시지",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())

    }

    private fun storeNotificationCache(mNotificationItems: ArrayList<NotificationItem?>?) {
        val notificationlist=mNotificationItems
        Log.d("SendMessageService 알림 list count", notificationlist!!.size.toString())
        val sharedPref: SharedPreferences = this.getSharedPreferences(AppTag.USER_INFO, 0)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        val gson = Gson()
        val json = gson.toJson(notificationlist)
        editor.putString(AppTag.NOTIFICATIONCACHE_TAG, json)
        Log.d("SendMessageService 알림 json", json)
        editor.apply()
    }
}