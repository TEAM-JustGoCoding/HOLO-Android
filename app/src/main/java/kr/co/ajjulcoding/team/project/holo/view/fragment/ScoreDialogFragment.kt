package kr.co.ajjulcoding.team.project.holo.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.AppTag
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity
import kr.co.ajjulcoding.team.project.holo.repository.Repository
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentScoreBinding

class ScoreDialogFragment() : DialogFragment() {
    private lateinit var _activity: MainActivity
    private val mActivity get() = _activity
    private lateinit var _userInfo: HoloUser
    private val userInfo get() = _userInfo
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)

        CoroutineScope(Dispatchers.Main).launch {
            val repository = Repository()
            val score = repository.updateUserScore(userInfo.uid!!)
            val count = repository.updateUserCount(userInfo.uid!!).toString()
            binding.countText.setText(count)
            binding.scoreText.setText(score)
        }

        val view = binding.root
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialBtn.setOnClickListener {
            dismiss()
            mActivity.changeFragment(AppTag.SETTING_TAG)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}