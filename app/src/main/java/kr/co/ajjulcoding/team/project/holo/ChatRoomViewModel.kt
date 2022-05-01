package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import kotlin.Exception

class ChatRoomViewModel: ViewModel() {
    private var listenerRgst:ListenerRegistration? = null
    private var _chatBubbleLi = MutableLiveData<ArrayList<ChatBubble>>()
    val chatBubbleLi:LiveData<ArrayList<ChatBubble>> get() = _chatBubbleLi
    private var _sendError = MutableLiveData<Exception>()
    private var _validScore: MutableLiveData<Boolean> = MutableLiveData(false)
    val validScore: LiveData<Boolean> get() = _validScore
    val sendError: LiveData<Exception> = _sendError
    private val repository = Repository()

    init {
        _chatBubbleLi.value = ArrayList<ChatBubble>()
    }

    fun setChatBubble(userData:HoloUser, chatData:SimpleChatRoom, content: String) = viewModelScope.launch {
        repository.setChatBubble(userData, chatData, content, _sendError)
    }

    fun getChatBubbleLi(title:String, randomDouble:Double) = viewModelScope.launch{
        if (listenerRgst == null)
            listenerRgst = repository.getChatBubbleLi(title, randomDouble, _chatBubbleLi)
        Log.d("채팅방 리스너 등록", listenerRgst.toString())
    }

    fun postScore(email:String, star:Float) = viewModelScope.launch {
        if (repository.postScore(email, star))
            _validScore.value = _validScore.value!!.not()
    }
//    override fun onCleared() {
//        super.onCleared()
//        listenerRgst?.remove()
//        listenerRgst = null
//    }
}