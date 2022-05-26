package kr.co.ajjulcoding.team.project.holo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class UtilityBillItem(var content: String?, var term: Int?, var day: Int?) : Parcelable {
//    var content: String? = content
//    var term: Int? = term
//    var day: Int? = day

    @JvmName("getContent1")
    fun getContent(): String? {
        return content
    }

    @JvmName("getTerm1")
    fun getTerm(): Int? {
        return term
    }

    @JvmName("getDay1")
    fun getDay(): Int? {
        return day
    }

    @JvmName("setContent1")
    fun setContent(content: String?) {
        this.content = content
    }

    @JvmName("setTerm1")
    fun setTerm(term: Int?) {
        this.term = term
    }

    @JvmName("setDay1")
    fun setDay(day: Int?) {
        this.day = day
    }
}