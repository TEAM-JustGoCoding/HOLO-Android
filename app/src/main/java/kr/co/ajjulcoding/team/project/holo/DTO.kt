package kr.co.ajjulcoding.team.project.holo

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HoloUser(val uid: String?, val realName: String, val nickName: String,
        var location:String? = null, var profileImg:String? = null):Serializable