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

class SendMessageService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {  // 알림 데이터 수신
        super.onMessageReceived(remoteMessage)

        Log.d("푸시 알림 받음", remoteMessage.toString())
        val sharedPref: SharedPreferences = this.getSharedPreferences(AppTag.USER_INFO, 0)
        val msgVaild: Boolean = sharedPref.getBoolean("msgValid", true)     // 알람 송수신 캐시 체크
        Log.d("서비스단 푸시 알림 수신", msgVaild.toString())
        if (msgVaild == false)
            return
        val randomNum:Double? = remoteMessage.data["randomNum"]?.toDouble() // TODO: 댓글/답글 데이터 들어오면 어떻게 되는지 찍어보기 -> null 반환?
        randomNum?.let {
            val currentNum: Double? = ChatRoomActivity.randomNum    // 현재 접속한 채팅방과 알림 온 방이 동일
            when (currentNum){
                remoteMessage.data["randomNum"]?.toDouble() -> return
                else -> {}
            }
        }

        val title = "Holo"
        val msg = remoteMessage.data["msg"]!! // ex. 댓글이 달렸습니다.
        val content = remoteMessage.data["content"]!! // ex. 8000원 정도 주문할 예정입니다.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
            sendNotificationInP(title, msg, content)
        else
            sendNotfication(title, remoteMessage.notification?.body!!)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun sendNotificationInP(title: String, msg: String, content: String){
        val intentMove = Intent(this, HoloSplashActivity::class.java)
        // TODO: 채팅, 댓글/답글 경우에 따라서 나누기
        SettingInApp.uniqueActivity(intentMove)
        val pendingIntent:PendingIntent = PendingIntent.getActivity(this, 0, intentMove,
        PendingIntent.FLAG_IMMUTABLE)

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
}