package kr.co.ajjulcoding.team.project.holo

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.firebase.Timestamp
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class HoloUser(val uid: String, val realName: String, val nickName: String,
        var location:String? = null, var profileImg:String? = null, var token:String? = null):Serializable

//채팅방 등록 TODO: 이것도 캐시로 저장해야할듯
data class ChatRoom(val title:String="",
                    val participant:ArrayList<String> = ArrayList<String>(),
                    val semail:String="", val snickName:String="", var stoken:String="",
                    val remail:String="", val rnickName:String="", var rtoken:String="",
                    var latestTime:Timestamp?= null,
                    val talkContent: ArrayList<String> = arrayListOf<String>(),
                    val talkNickName: ArrayList<String> = arrayListOf<String>(),
                    val talkTimestamp: ArrayList<Timestamp> = arrayListOf<Timestamp>(),
                    var sInputStar:Int? = null, var rInputStar:Int? = null)