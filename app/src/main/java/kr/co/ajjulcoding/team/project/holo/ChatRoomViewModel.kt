package kr.co.ajjulcoding.team.project.holo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatRoomViewModel: ViewModel() {
    private var _chatBubbleLi = MutableLiveData<ArrayList<String>>()
    private val chatBubbleLi:LiveData<ArrayList<String>> get() = _chatBubbleLi

    init {
        _chatBubbleLi.value = ArrayList<String>()
    }

    fun getChatBubbleLi(title:String, participants:ArrayList<String>){

    }
}