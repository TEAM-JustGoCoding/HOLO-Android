package kr.co.ajjulcoding.team.project.holo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import kr.co.ajjulcoding.team.project.holo.databinding.DialogFragmentPostScoreBinding

class PostScoreDialogFragment: DialogFragment() {
    private var _binding: DialogFragmentPostScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var postOnBtnClickListener: PostOnBtnClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFragmentPostScoreBinding.inflate(inflater, container, false)
        dialog?.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        dialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        binding.btnCancle.setOnClickListener { dismiss() }
        binding.btnOk.setOnClickListener {
            postOnBtnClickListener.PostOnBtnClicked(vaild = true)
            dismiss()
        }

        return binding.root
    }

    interface PostOnBtnClickListener
    {
        fun PostOnBtnClicked(vaild:Boolean)
    }

    fun setPostOnBtnClicked(buttonClickListener: PostOnBtnClickListener){
        this.postOnBtnClickListener = buttonClickListener
    }

}