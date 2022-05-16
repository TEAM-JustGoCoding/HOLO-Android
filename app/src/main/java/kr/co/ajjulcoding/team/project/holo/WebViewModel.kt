package kr.co.ajjulcoding.team.project.holo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class WebViewModel: ViewModel() {
    private val repository = Repository()

    fun sendCmtPushAlarm(body: CmtNotificationBody) = viewModelScope.launch {
        repository.sendCmtPushAlarm(body)
    }

    fun getUserNicknameAndToken(email: String) = viewModelScope.async{
        val nameToken:Pair<String,String> = repository.getUserNicknameAndToken(email)
        return@async nameToken
    }
}