package kr.co.ajjulcoding.team.project.holo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private var _chatRoom = MutableLiveData<ChatRoom>()
    val chatRoom:LiveData<ChatRoom> = _chatRoom
    val repository = Repository()

    fun getUserNicknameAndToken(email:String)= viewModelScope.async {
        return@async repository.getUserNicknameAndToken(email)
    }

    suspend fun createChatRoom(data:ChatRoom, mActivity: MainActivity) = viewModelScope.async{
        val valid = repository.createChatRoom(data, _chatRoom)
        if (!valid){
            return@async false
        }
        Log.d("채팅방 열기 버튼 클릭3", "들어옴")
        return@async true
    }
}