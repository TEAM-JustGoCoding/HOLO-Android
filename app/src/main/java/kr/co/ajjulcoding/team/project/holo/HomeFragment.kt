package kr.co.ajjulcoding.team.project.holo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentHomeBinding


class HomeFragment(val currentUser:HoloUser) : Fragment() {
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        binding.btnNotifi.setOnClickListener {
            // TODO("테스트를 위해 지금은 로그아웃 버튼으로 사용, 추후에 수정")
            SettingInApp.mAuth.signOut()
            requireActivity().finish()
        }
        binding.circleImageView.setOnClickListener {
            (requireActivity() as MainActivity).changeFragment(AppTag.PROFILE_TAG)
        }
        binding.textLocation.setOnClickListener {
            (requireActivity() as MainActivity).changeFragment(AppTag.GPS_TAG)
        }
    }

    private fun setProfile(){
        binding.textProfileName.setText(currentUser.nickName)
        if (currentUser.location == null){
            binding.textLocation.setText("위치 등록")
        }else
            binding.textLocation.setText(currentUser.location)
    }

    fun setUserLocation(location:String) {
        homeViewModel.setUserLocation(location)
        currentUser.location = location
    }

}