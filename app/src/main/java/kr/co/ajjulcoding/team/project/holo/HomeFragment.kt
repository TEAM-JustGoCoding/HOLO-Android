package kr.co.ajjulcoding.team.project.holo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentHomeBinding


class HomeFragment(val currentUser:HoloUser) : Fragment() {
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding

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

    private fun setProfile(){
        binding.textProfileName.setText(currentUser.nickName)
    }

}