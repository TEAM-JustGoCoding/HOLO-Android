package kr.co.ajjulcoding.team.project.holo.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.data.ChatRoom
import kr.co.ajjulcoding.team.project.holo.data.CmtNotificationBody
import kr.co.ajjulcoding.team.project.holo.repository.Repository
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity

class WebViewModel: ViewModel() {
    private var _chatRoom = MutableLiveData<ChatRoom>()
    val chatRoom: LiveData<ChatRoom> = _chatRoom
    val repository = Repository()

    fun getUserNicknameAndToken(email:String)= viewModelScope.async {
        return@async repository.getUserNicknameAndToken(email)
    }
    
    fun sendCmtPushAlarm(body: CmtNotificationBody) = viewModelScope.launch {
        repository.sendCmtPushAlarm(body)
    }

    suspend fun getId(email:String) = viewModelScope.async {
        val id:Int? = repository.getId(email)
        return@async id
    }

    suspend fun createChatRoom(data: ChatRoom, mActivity: MainActivity) = viewModelScope.async{
        val valid = repository.createChatRoom(data, _chatRoom)
        if (!valid){
            return@async false
        }
        Log.d("채팅방 열기 버튼 클릭3", "들어옴")
        return@async true
    }
}