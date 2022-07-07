package kr.co.ajjulcoding.team.project.holo.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity

abstract class BaseFragment<B: ViewBinding> : Fragment() {
    private var _binding: B? = null
    val binding get() = _binding!!
    private lateinit var _activity: MainActivity
    val mActivity get() = _activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getFragmentBinding(inflater, container)
        _activity = requireActivity() as MainActivity
        return binding.root
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}