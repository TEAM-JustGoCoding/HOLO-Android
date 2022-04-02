package kr.co.ajjulcoding.team.project.holo

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    private var _userLocation = MutableLiveData<String>()
    val userLocation: LiveData<String> = _userLocation
    private var _userProile = MutableLiveData<Uri>()
    val userProfile:LiveData<Uri> = _userProile

    fun setUserLocation(location:String){
        _userLocation.value = location
    }

    fun setUserProfile(imgUri: Uri){
        _userProile.value = imgUri
    }
}