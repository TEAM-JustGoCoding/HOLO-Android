package kr.co.ajjulcoding.team.project.holo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentAccountBinding


class AccountFragment(var currentUser:HoloUser) : Fragment() {
    private lateinit var _binding: FragmentAccountBinding
    private val binding get() = _binding
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView(){
        binding.editAccount.setText(currentUser.account)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

            Toast.makeText(mActivity, "계좌번호가 복사되었습니다", Toast.LENGTH_SHORT).show()
        }
        binding.btnEnroll.setOnClickListener {
            Toast.makeText(mActivity, "계좌번호가 등록되었습니다", Toast.LENGTH_SHORT).show()
            (requireActivity() as MainActivity).setAccount(binding.editAccount.text.toString())
            (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
        }
    }
}