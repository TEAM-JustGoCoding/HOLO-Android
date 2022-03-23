package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {
    private val retrofit = RetrofitClient.getInstance()

    suspend fun getUserInfo(email:String): HoloUser?{
        val server = retrofit.create(UserApiInterface::class.java)
        var result:HoloUser? = null
        CoroutineScope(Dispatchers.Main).launch {
            server.getUserInfo(email).enqueue(object: Callback<HoloUser>{
                override fun onResponse(
                    call: Call<HoloUser>,
                    response: Response<HoloUser>
                ) {
                    if (response.isSuccessful){
                        Log.d("로그인 데이터 정보", "성공: ${response.body()}")
                        result = response.body()

                    }else{
                        Log.d("로그인 데이터 정보", "통신 실패: ${response.body()}")
                    }
                }

                override fun onFailure(call: Call<HoloUser>, t: Throwable) {
                    Log.d("로그인 데이터 정보", "통신 실패(인터넷 끊김 등): ${t.localizedMessage}")
                }

            })
        }.join()
        return result
    }
}