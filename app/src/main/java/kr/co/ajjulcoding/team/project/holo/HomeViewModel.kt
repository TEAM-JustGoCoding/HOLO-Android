package kr.co.ajjulcoding.team.project.holo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private var _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> = _userLocation

    fun setUserLocation(location:String){
        _userLocation.value = location
    }
}