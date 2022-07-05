package kr.co.ajjulcoding.team.project.holo.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.data.ChatRoom
import kr.co.ajjulcoding.team.project.holo.repository.Repository

class ChatListViewModel: ViewModel() {
    private val repository = Repository()
    private var listenerRgst:ListenerRegistration? = null
    private var _userChatRoomLi = MutableLiveData<ArrayList<ChatRoom>>()
    val userChatRoomLi: LiveData<ArrayList<ChatRoom>> = _userChatRoomLi

    init {
        _userChatRoomLi.value = ArrayList<ChatRoom>()
    }

    fun getUserChatRoomLi(userEmail:String) = viewModelScope.launch{
        if (listenerRgst == null)
            listenerRgst = repository.getUserChatRoomLi(userEmail, _userChatRoomLi)
        Log.d("채팅목록 리스너 등록", listenerRgst.toString())
    }

    // TODO: 리스너 삭제 추가
}