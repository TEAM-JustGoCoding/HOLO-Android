package kr.co.ajjulcoding.team.project.holo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    private var _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> = _userLocation
    private var _userProile = MutableLiveData<Uri>()
    val userProfile:LiveData<Uri> = _userProile
    private var _chatRoom = MutableLiveData<ChatRoom>()
    val chatRoom:LiveData<ChatRoom> = _chatRoom
    val repository = Repository()

    fun setUserLocation(location:String){
        _userLocation.value = location
    }

    fun setUserProfile(imgUri: Uri){
        _userProile.value = imgUri
    }

    fun getUserNicknameAndToken(email:String)= viewModelScope.async {
        return@async repository.getUserNicknameAndToken(email)
    }

    suspend fun createChatRoom(data:ChatRoom) = viewModelScope.async{
        val valid = repository.createChatRoom(data)
        if (!valid){
            return@async false
        }
        _chatRoom.value = data
        return@async true
    }
}