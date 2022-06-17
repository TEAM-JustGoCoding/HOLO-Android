package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log

class Repository {

    suspend fun getUserInfo(email:String): HoloUser?{
        var result:HoloUser? = null
        val client = OkHttpClient()
        val mySearchUrl = (PhpUrl.DOTHOME+PhpUrl.URL_SELECT_USER).toHttpUrlOrNull()!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",email)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("로그인 데이터 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
                Log.d("로그인 데이터 정보!!", jsonobj.getString("nick_name"))
                result = HoloUser(
                    jsonobj.getInt("id"),
                    jsonobj.getString("uid"),
                    jsonobj.getString("real_name"),
                    jsonobj.getString("nick_name"),
                    jsonobj.getString("score"))
            }catch (e:IOException){
                Log.d("로그인 데이터 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        Log.d("이메일", email.toString())

        return result
    }

    suspend fun getId(email:String):Int?{
        var result:Int? = null
        val client = OkHttpClient()
        val mySearchUrl = (PhpUrl.DOTHOME+PhpUrl.URL_SELECT_USER).toHttpUrlOrNull()!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",email)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("로그인 데이터 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
                Log.d("로그인 데이터 정보!!", jsonobj.getString("id"))
                result = jsonobj.getInt("id")
            }catch (e:IOException){
                Log.d("로그인 데이터 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        Log.d("이메일", email.toString())

        return result
    }

    suspend fun deleteUserInfo(email:String): Boolean{
        var result:Boolean = false

        val client = OkHttpClient()
        val url = PhpUrl.DOTHOME+PhpUrl.URL_DELETE_USER
        val body: RequestBody = FormBody.Builder().add("uid", email).build() as RequestBody
        val request = Request.Builder().url(url).post(body).build()
        val FBstorage = FirebaseStorage.getInstance()
        val FBstorageRef = FBstorage.reference

        CoroutineScope(Dispatchers.IO).async {
            SettingInApp.mAuth.currentUser!!.delete().await()
            Log.d("삭제할 프로필 파일", "profile_img/profile_${email.replace(".","")}.jpg")
            FBstorageRef.child("profile_img/profile_${email.replace(".","")}.jpg")
                .delete().await()
        }.await()

        CoroutineScope(Dispatchers.IO).async {  // TODO: 문제 확인
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("탈퇴 데이터 정보", "성공: ${str_response}")
                result = str_response.toBoolean()
            }catch (e:IOException){
                Log.d("탈퇴 통신 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

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
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
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
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
                result = str_response.toBoolean()
            }catch (e:IOException){
                Log.d("닉네임 중복 통신 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        return result
    }

//    suspend fun dupliPhoneNum(number:String):Boolean{
//        var result:Boolean = false
//
//        coroutineScope {
//            SettingInApp.db.collection("phoneNumber").document("${chatRoomData.title} ${chatRoomData.randomDouble}")
//                .set(chatRoomData).addOnSuccessListener {
//                    _chatRoom.value = chatRoomData
//
//                    result = true
//                }
//                .addOnFailureListener {
//                    result = false
//                    Log.d("오류 발생","createChatRoom: $it")
//                }
//        }.await()
//    }

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
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
            } catch (e: IOException) {
                Log.d("토큰 데이터 전송!!", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }
    }

    suspend fun sendChatPushAlarm(notifiBody: ChatNotificationBody){
        val debug = RetrofitInstance.api.sendChatNotification(notifiBody)
        Log.d("채팅 알림 오류 확인", debug.toString())
    }

    suspend fun sendCmtPushAlarm(notifiBody: CmtNotificationBody){  // 댓글/답글 공용
        val debug = RetrofitInstance.api.sendCmtNotification(notifiBody)
        Log.d("채팅 알림 오류 확인", debug.toString())
    }



    suspend fun getUserNicknameAndToken(email:String):Pair<String,String>{
        val client = OkHttpClient()
        val mySearchUrl = (PhpUrl.DOTHOME+PhpUrl.URL_GET_TOKEN).toHttpUrlOrNull()!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",email)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()
        var nickName:String = ""
        var token:String = ""

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
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
        return Pair(nickName,token)
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
//                if (_userChatRoomLi.value!!.size == querySnapshot.size()) // 중복 방지 TODO: 리스너 단일로 만들고 삭제해보기
//                    return@addSnapshotListener
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
                        _userChatRoomLi.value = _userChatRoomLi.value
                    }else if (dcm.type == DocumentChange.Type.MODIFIED){
                        Log.d("채팅방 수정", "getUserChatRoomLi: ${_userChatRoomLi.value}  $dcm")
                        val roomData = dcm.document.toObject(ChatRoom::class.java) as ChatRoom
                        tempArray.removeIf { it.randomDouble == roomData.randomDouble }
                        tempArray.add(0, roomData)
                        _userChatRoomLi.value = tempArray
                    }
                    // TODO: 채팅방 거래 완료 이벤트 받으면 삭제 타입도 처리하기
                    //if (dcm.type == DocumentChange.Type.REMOVED)
                }
            }

        return listenerRgst
    }

    suspend fun setChatBubble(userData:HoloUser, chatRoomData: SimpleChatRoom, content: String
                              , _sendError:MutableLiveData<Exception>): Boolean{
        var vaild:Boolean = false
        coroutineScope {
            val dRef = SettingInApp.db.collection("chatRoom").document("${chatRoomData.title} ${chatRoomData.randomDouble}")
            SettingInApp.db.runTransaction { transition ->
                val timestamp:Timestamp = Timestamp.now()
                dRef.update("talkContent",FieldValue.arrayUnion(ChatBubble(userData.nickName, content, timestamp)))
                dRef.update("latestTime", timestamp)
            }.addOnSuccessListener {
                vaild = true
            }.addOnFailureListener {
                Log.d("채팅 트랜잭션 실패", it.toString())
                _sendError.value = it
            }
        }.await()
        return vaild
    }

    fun getChatBubbleLi(title:String, randomDouble:Double, _chatBubbleLi:MutableLiveData<ArrayList<ChatBubble>>)
    : ListenerRegistration{
        // TODO: 채팅 입력 기능 완성 후에 테스트하기
        Log.d("채팅방 데이터 들어오는지 확인", _chatBubbleLi.value.toString())
        val listenerRgst:ListenerRegistration = SettingInApp.db.collection("chatRoom")
            .whereEqualTo(AppTag.CHAT_TITLE, title)
            .whereEqualTo(AppTag.CHAT_RANDOM, randomDouble)
            .addSnapshotListener { querySnapshot, e ->
                Log.d("채팅방 오류 코드1", "getChatBubbleLi: $e")
                Log.d("채팅방 오류 코드2", "getChatBubbleLi: ${querySnapshot}")
                if (querySnapshot == null) return@addSnapshotListener
                querySnapshot.documentChanges.forEachIndexed { idx, dcm ->
                    Log.d("오류 코드", "getUserChatRoom: ${idx}  $dcm")
                    val fbBubbleLi:ArrayList<ChatBubble> = dcm.document.toObject(ChatRoom::class.java).talkContent
                    if (_chatBubbleLi.value!!.size == fbBubbleLi.size) { // 중복 방지 TODO: 리스너 단일로 만들고 삭제해보기
                        return@addSnapshotListener
                        return@forEachIndexed
                    }
                    var tempLi = ArrayList<ChatBubble>() // 깊은 복사(객체 영향 X)
                    tempLi.addAll(fbBubbleLi.reversed())
                    if (dcm.type == DocumentChange.Type.ADDED){
                        if (_chatBubbleLi.value!!.size == fbBubbleLi.size) {
                            return@addSnapshotListener
                            return@forEachIndexed
                        }
                        Log.d("말풍선 추가", "getUserChatRoomLi: ${fbBubbleLi[fbBubbleLi.lastIndex]}")

                        Log.d("말풍선 추가2", "getUserChatRoomLi: ${_chatBubbleLi.value}")
                    }else if (dcm.type == DocumentChange.Type.MODIFIED){
                        if (_chatBubbleLi.value!!.size == fbBubbleLi.size) {
                            return@addSnapshotListener
                            return@forEachIndexed
                        }
                        Log.d("말풍선 수정", "getUserChatRoomLi: ${dcm.document.toObject(ChatRoom::class.java)}  $dcm")
                    }
                    _chatBubbleLi.value = tempLi
                }
            }
        return listenerRgst
    }

    suspend fun checkValidStar(userEmail: String, chatTitle:String, chatRandom:Double): Pair<Boolean, String>{
        var vaild: Boolean = true
        var direction: String = "rinputStar"    // 현 사용자 방향
        coroutineScope {
            SettingInApp.db.collection("chatRoom")
                .document("${chatTitle} ${chatRandom}").get()
                .addOnSuccessListener { snapshot ->
                    val remail: String = snapshot["remail"] as String
                    val semail: String = snapshot["semail"] as String
                    Log.d("체커 이메일 확인", "$userEmail $remail $semail")
                    if (remail == userEmail){
                        snapshot["rinputStar"]?.let {
                            vaild = false
                        }
                    }else{
                        direction = "sinputStar"
                        snapshot["sinputStar"]?.let {
                            vaild = false
                        }
                    }
                }
                .addOnFailureListener {
                    Log.d("오류 발생","deleteChatRoom: $it")
                    vaild = false
                }
        }.await()
        return Pair(vaild, direction)
    }

    suspend fun postScore(userEmail:String, direction: String, star:Float,
                          chatTitle: String, chatRandom: Double): Boolean{
        var result: Boolean = false
        val client = OkHttpClient()
        val url = PhpUrl.DOTHOME + PhpUrl.URL_POST_SCORE
        val body: RequestBody = FormBody.Builder().add("uid", userEmail)
            .add("star", star.toString()).build() as RequestBody
        val request = Request.Builder().url(url).post(body).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()
                val str_reponse = response.body!!.string()
                val documentRef:DocumentReference = SettingInApp.db.collection("chatRoom")
                    .document("${chatTitle} ${chatRandom}")
                Log.d("별점 등록 데이터 전송", str_reponse)
                documentRef.update(direction, true).addOnSuccessListener {
                }.await()
            documentRef.get().addOnSuccessListener { snp ->
                Log.d(" 별점 등록 사용자 확인", "${snp["rinputStar"]} ${snp["sinputStar"]}")
                if ((snp["rinputStar"] != null) && (snp["sinputStar"] != null)){
                    Log.d(" 별점 등록 사용자 올", "${snp["rinputStar"]} ${snp["sinputStar"]}")
                    result = true
                }
                }.await()   // await로 나열하지 않고 블록 안으로 넣으면 각각의 스레드로 판별해서 흐름 통일 안됨
            }catch (e: IOException){
                Log.d("별점 등록 데이터 전송!!", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        Log.d("별점 등록 결과", result.toString())
        return result
    }

    suspend fun deleteChatRoom(chatTitle:String, chatRandom:Double):Boolean{    // 채팅방 삭제 체커
        var result:Boolean = false
        coroutineScope {
            SettingInApp.db.collection("chatRoom")
                .document("${chatTitle} ${chatRandom}").delete()
                .addOnSuccessListener { result = true } // TODO: 상대방도 별점 등록했으면 방 삭제
                .addOnFailureListener { Log.d("오류 발생","deleteChatRoom: $it") }
        }.await()
        return result
    }

    suspend fun updateUserScore(uid: String): String{
        var result:String = ""

        val client = OkHttpClient()
        val mySearchUrl = (PhpUrl.DOTHOME+PhpUrl.URL_UPDATE_SCORE).toHttpUrlOrNull()!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",uid)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("평점 데이터 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
                result = jsonobj.getString("score")
            }catch (e:IOException){
                Log.d("평점 데이터 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        return result
    }

    suspend fun updateUserCount(uid: String): Int{
        var result:Int = 0

        val client = OkHttpClient()
        val mySearchUrl = (PhpUrl.DOTHOME+PhpUrl.URL_UPDATE_COUNT).toHttpUrlOrNull()!!.newBuilder()
        mySearchUrl.addQueryParameter("uid",uid)
        val request = Request.Builder().url(mySearchUrl.build().toString()).build()

        CoroutineScope(Dispatchers.IO).async {
            try {
                val response = client.newCall(request).execute()   // 동기로 실행
                val str_response = response.body!!.string()   // string()은 딱 한 번만 호출 가능
                Log.d("거래횟수 데이터 정보", "성공: ${str_response}")
                val jsonobj = JSONObject(str_response)
                result = jsonobj.getInt("deal_count")
            }catch (e:IOException){
                Log.d("거래횟수 데이터 정보", "통신 실패(인터넷 끊김 등): ${e}")
            }
        }.await()

        return result
    }
}