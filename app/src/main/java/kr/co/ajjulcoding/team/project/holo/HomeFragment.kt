package kr.co.ajjulcoding.team.project.holo

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentHomeBinding


class HomeFragment(val currentUser:HoloUser) : Fragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("홈 프래그먼트", "onCreate")
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setProfile()
        Log.d("홈 프래그먼트", "onCreateView")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("홈 프래그먼트", "onViewCreated")
        homeViewModel.userLocation.observe(viewLifecycleOwner){
            binding.textLocation.setText(it)
        }
        homeViewModel.userProfile.observe(viewLifecycleOwner){imgUri -> // TODO: 사용자 정보 수정될 때 호출되게
            Glide.with(_activity).load(imgUri).apply {
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }.into(binding.circleImageView)
        }
        homeViewModel.chatRoom.observe(viewLifecycleOwner){
            mActivity.showAlertDialog("채팅방이 개설되었습니다!", *arrayOf("확인"))
        }

        binding.btnTouchSell.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("채팅방 열기 버튼 클릭", "들어옴")
                val receiverEmail = "lyy8201@gmail.com"
                val rNicknameAndToken = homeViewModel.getUserNicknameAndToken(receiverEmail)
                val receiverNickname = rNicknameAndToken.await().first
                val receiverToken = rNicknameAndToken.await().second
                val chatRoomData = ChatRoom("힐스테이트 커피시켜 먹으실 분", arrayListOf(currentUser.uid, receiverEmail)
                    , currentUser.uid!!, currentUser.nickName
                    ,currentUser.token ?: "토큰캐시없음", receiverEmail, receiverNickname, receiverToken
                    , Timestamp.now())
                val valid = homeViewModel.createChatRoom(chatRoomData, mActivity)
                if (!(valid.await())){
                    mActivity.showAlertDialog("네트워크 연결을 확인할 수 없습니다!", *arrayOf("확인"))
                    return@launch
                }
            }
        }
        binding.btnNotifi.setOnClickListener {
            // TODO("테스트를 위해 지금은 로그아웃 버튼으로 사용, 추후에 수정")
            SettingInApp.mAuth.signOut()
            mActivity.finish()
        }
        binding.circleImageView.setOnClickListener {
            mActivity.changeFragment(AppTag.PROFILE_TAG)
        }
        binding.textLocation.setOnClickListener {
            mActivity.changeFragment(AppTag.GPS_TAG)
        }
    }

    private fun setProfile(){
        Log.d("실행", "onRequestPermissionsResult() _ 권한 허용")
        binding.textProfileName.setText(currentUser.nickName)
        if (currentUser.location == null){
            binding.textLocation.setText("위치 등록")
        }else
            binding.textLocation.setText(currentUser.location)
        currentUser.profileImg?.let {
            Glide.with(_activity).load(Uri.parse(it)).apply {
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }.into(binding.circleImageView)
        }
    }

    fun setUserLocation(location:String) {
        homeViewModel.setUserLocation(location)
        currentUser.location = location
    }

    fun setUserProfile(url:String){
        currentUser.profileImg = url
    }

    fun setUserAccount(account:String){
        currentUser.account = account
    }
}