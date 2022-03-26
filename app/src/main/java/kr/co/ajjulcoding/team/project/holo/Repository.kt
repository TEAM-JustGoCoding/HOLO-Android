package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class Repository {

    suspend fun getUserInfo(email:String): HoloUser?{
        var result:HoloUser? = null


        val client = OkHttpClient()
        val mySearchUrl = HttpUrl.parse(PhpUrl.DOTHOME+PhpUrl.ULR_SELECT_USER)!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",email)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("로그인 데이터 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
                Log.d("로그인 데이터 정보!!", jsonobj.getString("nick_name"))
                result = HoloUser(jsonobj.getString("uid"),
                    jsonobj.getString("real_name"),
                    jsonobj.getString("nick_name"))
            }catch (e:IOException){
                Log.d("로그인 데이터 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()
//        client.newCall(request).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                Log.d("로그인 데이터 정보", "통신 실패(인터넷 끊김 등): ${e}")
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
//                Log.d("로그인 데이터 정보", "성공: ${str_response}")
//                val jsonobj = JSONObject(str_response)
//                Log.d("로그인 데이터 정보!!", jsonobj.getString("nick_name"))
//                result = HoloUser(jsonobj.getString("uid"),
//                                jsonobj.getString("real_name"),
//                                jsonobj.getString("nick_name"))
//            }
//
//        })
        Log.d("이메일", email.toString())

        return result
    }

    suspend fun checkNameDupli(nickName:String): Boolean{
        var result:Boolean = false

        val client = OkHttpClient()
        val url = PhpUrl.DOTHOME+PhpUrl.URL_NICKNAME_DUPI
        val body: RequestBody = FormBody.Builder().add("nick_name", nickName).build() as RequestBody
        val request = Request.Builder().url(url).post(body).build()

        CoroutineScope(Dispatchers.IO).async {  // 메인스레드에서 네트워크 접근 금지 되어있어서 코루틴 사용
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("닉네임 데이터 정보", "성공: ${str_response}")
                result = str_response.toBoolean()
            }catch (e:IOException){
                Log.d("닉네임 중복 통신 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        return result
    }

    suspend fun insertRegister(userInfo: HoloUser): Boolean{
        var result: Boolean = false

        val client = OkHttpClient()
        val url = PhpUrl.DOTHOME+PhpUrl.URL_REGISTER
        val body: RequestBody = FormBody.Builder().add("uid", userInfo.uid)
            .add("nick_name", userInfo.nickName)
            .add("real_name", userInfo.realName).build() as RequestBody
        val request = Request.Builder().url(url).post(body).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("회원가입 데이터 정보!!", str_response)
                result = str_response.toBoolean()
            }catch (e:IOException){
                Log.d("닉네임 중복 통신 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        return result
    }
}