package kr.co.ajjulcoding.team.project.holo

import android.app.DownloadManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class Repository {

    suspend fun getUserInfo(email:String): HoloUser?{
        var result:HoloUser? = null


        val client = OkHttpClient()
        val mySearchUrl = HttpUrl.parse(PhpUrl.DOTHOME+PhpUrl.URL_SELECT_USER)!!.newBuilder()
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
                result = str_response.toBoolean()
            }catch (e:IOException){
                Log.d("닉네임 중복 통신 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        return result
    }

    suspend fun setToken(userEmail:String): String?{
        var token:String? = null
        FirebaseMessaging.getInstance().token.addOnSuccessListener { task ->
            token = task
            token?.let {
                postToken(userEmail,it)
            }
        }.await()
        return token
    }

    fun postToken(email:String, token:String){
        val client = OkHttpClient()
        val url = PhpUrl.DOTHOME+PhpUrl.URL_POST_TOKEN
        val body: RequestBody = FormBody.Builder().add("uid", email)
            .add("token", token).build() as RequestBody
        val request = Request.Builder().url(url).post(body).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
            } catch (e: IOException) {
                Log.d("토큰 데이터 정보!!", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }
    }

    suspend fun getUserNicknameAndToken(email:String):Pair<String,String>{
        val client = OkHttpClient()
        val mySearchUrl = HttpUrl.parse(PhpUrl.DOTHOME+PhpUrl.URL_GET_TOKEN)!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",email)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()
        var nickName:String? = null
        var token:String? = null

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("데베 토큰 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
                Log.d("데베 토큰 정보!!", jsonobj.getString("uid"))
                nickName = jsonobj.getString("nick_name")
                token = jsonobj.getString("token")
            }catch (e:IOException){
                nickName = "통신실패"
                token = "통신실패"
                Log.d("데베 토큰 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()
        Log.d("데베 토큰 변수 전달", "성공: ${token}")
        return Pair(nickName!!,token!!)
    }

    suspend fun createChatRoom(chatRoomData:ChatRoom):Boolean{
        var result = false

        coroutineScope {
            SettingInApp.db.collection("chatRoom").document()
                .set(chatRoomData).addOnSuccessListener { result = true }
                .addOnFailureListener {
                    result = false
                    Log.d("오류 발생","createChatRoom: $it")
                }
        }.await()
        Log.d("채팅방 생성 결과", result.toString())
        return result
    }

    suspend fun getUserChatRoomLi(userEmail:String, userChatRoomLi:MutableLiveData<ArrayList<ChatRoom>>)
    :  ListenerRegistration{
        // 사용자가 대표일 때와 참가자일 때 모두 고려
        val listenerRgst:ListenerRegistration = SettingInApp.db.collection("chatRoom").whereEqualTo(AppTag.SENDER_EMAIL, userEmail)
            .whereEqualTo(AppTag.RECEIVER_EMAIL, userEmail)
            .orderBy(AppTag.LATEST_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                Log.d("오류 코드", "getUserChatRoomLi: $e")
                if (querySnapshot == null) return@addSnapshotListener
                if (userChatRoomLi.value!!.size == querySnapshot.size()) // 중복 방지 TODO: 리스너 단일로 만들고 삭제해보기
                    return@addSnapshotListener
                val tempArray:ArrayList<ChatRoom> = userChatRoomLi.value!!
                querySnapshot.documentChanges.forEachIndexed { idx, dcm ->
                    if (dcm.type == DocumentChange.Type.ADDED){
                        tempArray.add(0, dcm.document.toObject(ChatRoom::class.java))
                        userChatRoomLi.value = tempArray
                    }
                    // TODO: 채팅방 거래 완료 이벤트 받으면 삭제 타입도 처리하기
                    //if (dcm.type == DocumentChange.Type.REMOVED)
                }
            }

        return listenerRgst
    }
}