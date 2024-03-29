package kr.co.ajjulcoding.team.project.holo.view.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.co.ajjulcoding.team.project.holo.AppTag
import kr.co.ajjulcoding.team.project.holo.base.BaseFragment
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentAccountBinding
import kr.co.ajjulcoding.team.project.holo.util.ToastUtil


class AccountFragment() : BaseFragment<FragmentAccountBinding>() {
    private lateinit var _userInfo: HoloUser
    private val userInfo get() = _userInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAccountBinding {
        return FragmentAccountBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        binding.btnBack.setOnClickListener {
            mActivity.changeFragment(AppTag.SETTING_TAG)
        }
        binding.btnCopy.setOnClickListener {
            val editEmail = binding.editAccount
            val inputAccount:String = editEmail.text.toString()
            val clipboard: ClipboardManager =
                requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", inputAccount)
            clipboard.setPrimaryClip(clip)

            if(binding.editAccount.length()==0) {
                ToastUtil.showToast(mActivity, "계좌번호를 입력해주세요")
            }
            else {
                ToastUtil.showToast(mActivity, "계좌번호가 복사되었습니다")
            }
        }
        binding.btnEnroll.setOnClickListener {
            if(binding.editAccount.length()==0) {
                ToastUtil.showToast(mActivity, "계좌번호를 입력해주세요")
            }
            else {
                ToastUtil.showToast(mActivity, "계좌번호가 등록되었습니다")
                (requireActivity() as MainActivity).setAccount(binding.editAccount.text.toString())
                (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
            }
        }
    }

    private fun initView(){
        binding.editAccount.setText(userInfo.account)
    }
}