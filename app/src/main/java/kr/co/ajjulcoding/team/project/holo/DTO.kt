package kr.co.ajjulcoding.team.project.holo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HoloUser {
    // Firebase에서 패스워드 관리하기 때문에 PHP로 가져올 필요 X
    // Json에서 uid라는 이름으로 var uid 표출
    @Expose
    @SerializedName("uid") private var uid:String? = null

    @Expose
    @SerializedName("real_name") private var realName:String? = null

    @Expose
    @SerializedName("nick_name") private var nickName:String? = null

//    @Expose
//    @SerializedName("success") private var success:Boolean = false
//
//    @Expose
//    @SerializedName("message") private var message:String? = null
    // 데이터 가져오기
    fun getUid() = uid
    fun getRealName() = realName
    fun getNickName() = nickName
//    fun getSuccess() = success
//    fun getMessage() = message
    // 데이터 삽입하기
    fun setUid(uid:String){ this.uid = uid }
    fun setRealName(realName:String){ this.realName = realName }
    fun setNickName(nickName:String){ this.nickName = nickName }
//    fun setSuccess(success:Boolean){ this.success = success }
//    fun setMessage(message:String){ this.message = message }
}