package kr.co.ajjulcoding.team.project.holo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UsersettingViewModel : ViewModel() {
    private var _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> = _userLocation

    fun setUserLocation(location:String){
        _userLocation.value = location
    }
}