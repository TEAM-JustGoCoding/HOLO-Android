package kr.co.ajjulcoding.team.project.holo

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.firebase.Timestamp
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class HoloUser(val uid: String, val realName: String, val nickName: String,
        var location:String? = null, var profileImg:String? = null, var token:String? = null):Parcelable

//채팅방 등록 TODO: 이것도 캐시로 저장해야할듯
@Parcelize  // Serializable하려면 다른 객체에도 적용시켜야함. 따라서 Parcelable로 대체
data class ChatRoom(val title:String="",
                    val participant:ArrayList<String> = ArrayList<String>(),
                    val semail:String="", val snickName:String="", var stoken:String="",
                    val remail:String="", val rnickName:String="", var rtoken:String="",
                    var latestTime:Timestamp?= null,
                    val talkContent: ArrayList<ChatBubble> = arrayListOf<ChatBubble>(),
                    val randomDouble:Double = Math.random(),
                    var sInputStar:Int? = null, var rInputStar:Int? = null):Parcelable

@Parcelize
data class SimpleChatRoom(val title:String="", val participant:ArrayList<String> = ArrayList<String>(),
                          var randomDouble:Double? = null,
                          val semail:String="", val snickName:String="", var stoken:String="",
                          val remail:String="", val rnickName:String="", var rtoken:String="",): Parcelable

@Parcelize
data class ChatBubble(val nickname:String? = null, val content:String = "", val currentTime:Timestamp? = null):Parcelable