package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class SettingInApp {
    companion object{
        val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
        fun uniqueActivity(intent:Intent){
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}

class User {
    companion object {
        const val USER_EMAIL = "userEmail"
        const val USER_REAL_NAME = "userRealName"
        const val USER_NICK_NAME = "userNickName"
        fun currentUserEmail() = SettingInApp.mAuth.currentUser?.email
    }
}

class PhpUrl {
    companion object{
        const val DOTHOME:String = "http://holo.dothome.co.kr/"
        const val URL_CREATE_REGISTER:String = "create_register.php"   // TODO("php 파일 연동")
        const val URL_GET_USER:String = "get_user.php"
    }
}

object RetrofitClient{  // singleton으로 동작
    private var retrofit:Retrofit? = null
    private val gson = GsonBuilder().setLenient().create()

    fun getInstance():Retrofit{
        if ( retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(PhpUrl.DOTHOME)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }
}



interface UserApiInterface{
    // TODO("아이디, 닉네임 중복 체크 확인 변수 php에서 어떤 이름으로 주고 받을지 규리와 상의")
    @FormUrlEncoded
    @POST(PhpUrl.URL_CREATE_REGISTER)
    fun createRegister(
        @Field("uid") uid:String,
        @Field("password") password:String,
        @Field("real_name") realName:String,
        @Field("nick_name") nickName:String,
    ):Call<HoloUser>

    @GET(PhpUrl.URL_GET_USER)
    fun getUserInfo(    // email을 이용해 사용자 정보 가져오기
        @Query("uid") uid:String
    ):Call<HoloUser>

}
