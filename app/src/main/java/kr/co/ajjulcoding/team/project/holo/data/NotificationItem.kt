package kr.co.ajjulcoding.team.project.holo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class NotificationItem(var name: String?, var message: String?, var url: String?) : Parcelable {
//    var name: String? = name
//    var message: String? = message

    @JvmName("getMessage1")
    fun getMessage(): String? {
        return message
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("getURL1")
    fun getURL(): String? {
        return url
    }

    @JvmName("setMessage1")
    fun setMessage(message: String?) {
        this.message = message
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }
}