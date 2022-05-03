package kr.co.ajjulcoding.team.project.holo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentScoreBinding

class ScoreFragment(var currentUser:HoloUser) : DialogFragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private var _binding: FragmentScoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentScoreBinding.inflate(inflater, container, false)
        binding.scoreText.setText(currentUser.score)
        val view = binding.root
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.updateBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val repository = Repository()
                val result = repository.updateUserScore(currentUser.uid!!)
                binding.scoreText.setText(result)
            }

        }
        binding.dialBtn.setOnClickListener {
//            dismiss()   //대화상자 닫는 함수
//            mActivity.changeFragment("dialog")
            mActivity.changeFragment(AppTag.SETTING_TAG)
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}