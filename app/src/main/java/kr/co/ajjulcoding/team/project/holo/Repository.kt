package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

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
        // onSuccess 리스너 사용해서 await 주니까 항상 성공한다는 보장이 없어서인지 await 안 걸림 따라서 비동기 컨트롤이
        // 안되기 때문에 addOnComplete 사용
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                token = it.result
                postToken(userEmail,it.result)
                return@addOnCompleteListener
            }
        }.await()
        Log.d("토큰 생성 확인2",token.toString())
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
        Log.d("이메일 받은 거 확인", email.toString())
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()
        var nickName:String? = null
        var token:String? = null

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body()!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("데베 토큰 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
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

    suspend fun createChatRoom(chatRoomData:ChatRoom, _chatRoom:MutableLiveData<ChatRoom>):Boolean{
        var result = false

        coroutineScope {
            SettingInApp.db.collection("chatRoom").document("${chatRoomData.title} ${chatRoomData.randomDouble}")
                .set(chatRoomData).addOnSuccessListener {
                    _chatRoom.value = chatRoomData

                    result = true
                }
                .addOnFailureListener {
                    result = false
                    Log.d("오류 발생","createChatRoom: $it")
                }
        }.await()
        Log.d("채팅방 생성 결과", result.toString())
        return result
    }

    fun getUserChatRoomLi(userEmail:String, _userChatRoomLi:MutableLiveData<ArrayList<ChatRoom>>)
    :  ListenerRegistration{
        Log.d("데이터 들어오는지 확인",userEmail)
        // 사용자가 대표일 때와 참가자일 때 모두 고려
        val listenerRgst:ListenerRegistration = SettingInApp.db.collection("chatRoom")
            .whereArrayContains(AppTag.PARTICIPANT, userEmail)
            .orderBy(AppTag.LATEST_TIME, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                Log.d("오류 코드", "getUserChatRoomLi: $e")
                Log.d("오류 코드", "getUserChatRoomLi: ${querySnapshot}")
                if (querySnapshot == null) return@addSnapshotListener
                if (_userChatRoomLi.value!!.size == querySnapshot.size()) // 중복 방지 TODO: 리스너 단일로 만들고 삭제해보기
                    return@addSnapshotListener
                val tempArray:ArrayList<ChatRoom> = _userChatRoomLi.value!!
                querySnapshot.documentChanges.forEachIndexed { idx, dcm ->
                    Log.d("오류 코드", "getUserChatRoomLi: ${idx}  $dcm")
                    if (dcm.type == DocumentChange.Type.ADDED){
                        Log.d("채팅방 추가", "getUserChatRoomLi: ${dcm.document.toObject(ChatRoom::class.java)}  $dcm")
                        tempArray.add(0, dcm.document.toObject(ChatRoom::class.java))
                        _userChatRoomLi.value = tempArray
                    }else if(dcm.type == DocumentChange.Type.REMOVED){
                        Log.d("채팅방 제거", "getUserChatRoomLi: ${dcm.document.toObject(ChatRoom::class.java)}  $dcm")
                        val reObject = dcm.document.toObject(ChatRoom::class.java)
                        val reIdx = _userChatRoomLi.value?.filter { it.randomDouble == reObject.randomDouble }?.get(0)
                        Log.d("채팅방 제거 인덱스", reIdx.toString())
                        _userChatRoomLi.value?.remove(reIdx)
                    }
                    // TODO: 채팅방 거래 완료 이벤트 받으면 삭제 타입도 처리하기
                    //if (dcm.type == DocumentChange.Type.REMOVED)
                }
            }

        return listenerRgst
    }

    suspend fun setChatBubble(userData:HoloUser, chatRoomData: SimpleChatRoom, content: String
                              , _sendError:MutableLiveData<Exception>){
        coroutineScope {
            val dRef = SettingInApp.db.collection("chatRoom").document("${chatRoomData.title} ${chatRoomData.randomDouble}")
//            SettingInApp.db.runTransaction { transition ->
//                val snapshot = transition.get(dRef)
//                dRef.update("talkContent",FieldValue.arrayUnion(ChatBubble(userData.nickName, content, Timestamp.now())))
//                dRef.update("talkNickName", FieldValue.arrayUnion(userData.nickName))
//                dRef.update("talkTimestamp",FieldValue.arrayUnion(Timestamp.now()))
//            }.addOnFailureListener {
//                Log.d("채팅 트랜잭션 실패", it.toString())
//                _sendError.value = it
//            }
            dRef.update("talkContent",FieldValue.arrayUnion(ChatBubble(userData.nickName, content, Timestamp.now())))

        }.await()
        return
    }

    fun getChatBubbleLi(title:String, randomDouble:Double, _chatBubbleLi:MutableLiveData<ChatRoom>)
    : ListenerRegistration{
        // TODO: 채팅 입력 기능 완성 후에 테스트하기
        Log.d("채팅방 데이터 들어오는지 확인", _chatBubbleLi.value.toString())
        val listenerRgst:ListenerRegistration = SettingInApp.db.collection("chatRoom")
            .whereEqualTo(AppTag.CHAT_TITLE, title)
            .whereEqualTo(AppTag.CHAT_RANDOM, randomDouble)
            .addSnapshotListener { querySnapshot, e ->
                Log.d("채팅방 오류 코드1", "getUserChatRoomLi: $e")
                Log.d("채팅방 오류 코드2", "getUserChatRoomLi: ${querySnapshot}")
                if (querySnapshot == null) return@addSnapshotListener
                querySnapshot.documentChanges.forEachIndexed { idx, dcm ->
                    Log.d("오류 코드", "getUserChatRoom: ${idx}  $dcm")
                    if (dcm.type == DocumentChange.Type.ADDED){
                        Log.d("채팅방 추가", "getUserChatRoomLi: ${dcm.document.toObject(ChatRoom::class.java)}  $dcm")
                        val chatRoomObj = dcm.document.toObject(ChatRoom::class.java)
                        _chatBubbleLi.value = chatRoomObj
                    }else if (dcm.type == DocumentChange.Type.MODIFIED){
                        Log.d("채팅방 수정", "getUserChatRoomLi: ${dcm.document.toObject(ChatRoom::class.java)}  $dcm")
                        val chatRoomObj = dcm.document.toObject(ChatRoom::class.java)
                        _chatBubbleLi.value = chatRoomObj
                    }
                }
            }
        return listenerRgst
    }
}