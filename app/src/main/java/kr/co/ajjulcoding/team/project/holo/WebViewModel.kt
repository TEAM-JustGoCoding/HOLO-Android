package kr.co.ajjulcoding.team.project.holo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WebViewModel: ViewModel() {
    private val repository = Repository()

    fun sendCmtPushAlarm(body: CmtNotificationBody) = viewModelScope.launch {
        repository.sendCmtPushAlarm(body)
    }
}