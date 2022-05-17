package kr.co.ajjulcoding.team.project.holo

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.firebase.Timestamp
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.io.Serializable

@Parcelize
data class HoloUser(val uid: String, val realName: String, val nickName: String, var score: String,
                    var location:String? = null, var profileImg:String? = null, var account:String? = null,
                    var token:String? = null, var utilitylist:ArrayList<UtilityBillItem>? = null):Parcelable

//채팅방 등록 TODO: 이것도 캐시로 저장해야할듯
@Parcelize  // Serializable하려면 다른 객체에도 적용시켜야함. 따라서 Parcelable로 대체
data class ChatRoom(val title:String="",
                    val participant:ArrayList<String> = ArrayList<String>(),
                    val semail:String="", val snickName:String="", var stoken:String="",
                    val remail:String="", val rnickName:String="", var rtoken:String="",
                    var latestTime:Timestamp?= null,
                    val talkContent: ArrayList<ChatBubble> = arrayListOf<ChatBubble>(),
                    val randomDouble:Double = Math.random(),
                    var sinputStar:Boolean? = null, var rinputStar:Boolean? = null):Parcelable

@Parcelize
data class SimpleChatRoom(val title:String="", val participant:ArrayList<String> = ArrayList<String>(),
                          var randomDouble:Double? = null,
                          val semail:String="", val snickName:String="", var stoken:String="",
                          val remail:String="", val rnickName:String="", var rtoken:String="",): Parcelable

@Parcelize
data class ChatBubble(val nickname:String? = null, val content:String = "", val currentTime:Timestamp? = null):Parcelable


data class ChatNotificationBody(val to: String, val data: ChatNotificationData){
    data class ChatNotificationData(val msg: String, val content: String, val randomNum: Double)
}

data class CmtNotificationBody(val to: String, val data: CmtNotificationData){  // 댓글/답글 공용
    data class CmtNotificationData(val msg: String, val content: String)
}

interface FcmInterface{ // 푸시 메시지를 서버로 보냄
    @POST("fcm/send")
    suspend fun sendChatNotification(   // 서버 통신은 비동기 처리
        @Body notification: ChatNotificationBody
    ) : retrofit2.Response<ResponseBody>

    @POST("fcm/send")
    suspend fun sendCmtNotification(   // 서버 통신은 비동기 처리
        @Body notification: CmtNotificationBody
    ) : retrofit2.Response<ResponseBody>
}

// 레트로핏 생성: 서버와 HTTP 통신을 해서 받은 데이터 앱에서 보여줌
object RetrofitInstance{
    class AppInterceptor: Interceptor {  // 인증토큰 필요하기 때문에 헤더 추가
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "key=${SettingInApp.SERVER_KEY}")
                .addHeader("Content-Type",SettingInApp.CONTENT_TYPE)
                .build()
            proceed(newRequest)
        }
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SettingInApp.FCM_BASE_URL)
            .client(provideOkHttpClient(AppInterceptor()))
            .addConverterFactory(GsonConverterFactory.create())  // JSON 타입 결과를 객체로 매핑
            .build()
    }

    val api: FcmInterface by lazy{
        retrofit.create(FcmInterface::class.java)
    }

    // 클라이언트
    private fun provideOkHttpClient(interceptor: AppInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .run {
            addInterceptor(interceptor) // httpResponse 받아옴
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            build()
        }
}