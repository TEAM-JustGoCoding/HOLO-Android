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
}