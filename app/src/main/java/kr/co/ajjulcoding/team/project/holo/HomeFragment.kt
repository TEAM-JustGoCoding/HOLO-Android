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
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentHomeBinding


class HomeFragment(val currentUser:HoloUser) : Fragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setProfile()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.btnTouchSell.setOnClickListener {
//            val data = ChatRoom("힐스테이트 커피시켜 먹으실 분", "leeyeah8245", )
//            homeViewModel.createChatRoom()
            // TODO: 데베 두개에 접근해서/ 채팅 목록, 방 UI로 띄우게 하기
            mActivity.showAlertDialog("채팅방이 개설됐습니다!", *arrayOf("확인"))
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

}