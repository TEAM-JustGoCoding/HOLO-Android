package kr.co.ajjulcoding.team.project.holo.view.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kr.co.ajjulcoding.team.project.holo.*
import kr.co.ajjulcoding.team.project.holo.base.BaseFragment
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentUsersettingBinding
import kr.co.ajjulcoding.team.project.holo.util.ToastUtil
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity
import kr.co.ajjulcoding.team.project.holo.view.viewmodel.UsersettingViewModel


class UsersettingFragment() : BaseFragment<FragmentUsersettingBinding>() {
    private lateinit var _userInfo: HoloUser
    private val userInfo get() = _userInfo
    private val usersettingViewModel: UsersettingViewModel by viewModels<UsersettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
    }


    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUsersettingBinding {
        return FragmentUsersettingBinding.inflate(layoutInflater,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        // TODO: 창설에서는 있어야 됨
        //binding.textAccount.visibility = View.GONE
        usersettingViewModel.userLocation.observe(viewLifecycleOwner){
            binding.textLocation.setText(it)
        }
        binding.profilePhoto.setOnClickListener {
            mActivity.checkPermissionForStorage(requireActivity())
        }
        binding.textScore.setOnClickListener {
            mActivity.supportFragmentManager?.let{fragmentManager ->
                val dialog: ScoreDialogFragment = ScoreDialogFragment()
                var userInfoBundle = Bundle()
                userInfoBundle.putParcelable(AppTag.USER_INFO, userInfo)
                dialog.arguments = userInfoBundle
                if (null == fragmentManager.findFragmentByTag(AppTag.SCORE_TAG)) {
                    dialog.show(fragmentManager, AppTag.SCORE_TAG)
                }
            }
        }
        binding.textLocationSet.setOnClickListener {
            mActivity.checkPermissionForLocation(requireActivity())
        }
        binding.textUtilityBill.setOnClickListener {
            mActivity.supportFragmentManager?.let{fragmentManager ->
                val dialog: UtilityBillDialogFragment = UtilityBillDialogFragment()
                var userInfoBundle = Bundle()
                userInfoBundle.putParcelable(AppTag.USER_INFO, userInfo)
                dialog.arguments = userInfoBundle
                if (null == fragmentManager.findFragmentByTag(AppTag.UTILITYBILLDIALOG_TAG)) {
                    dialog.show(fragmentManager, AppTag.UTILITYBILLDIALOG_TAG)
                }
            }
        }
        binding.textAccount.setOnClickListener {
            mActivity.changeFragment(AppTag.ACCOUNT_TAG)
        }
        binding.switchBell.isChecked = userInfo.msgVaild
        binding.switchBell.setOnCheckedChangeListener { _, isChecked ->
            setMsgValid(isChecked)
        }
        binding.textLogout.setOnClickListener {
            AlertDialog.Builder(mActivity)
                .setTitle("로그아웃 하시겠습니까?")
                .setItems(arrayOf("예","아니오"), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, idx: Int) {
                        dialog!!.dismiss()
                        if (idx == 0){
                            SettingInApp.mAuth.signOut()
                            ToastUtil.showToast(requireActivity(), "로그아웃 되었습니다.")
                            mActivity.changetoLoginActivity()
                        }
                    }
                })
                .create()
                .show()
        }
        binding.textWithdrawal.setOnClickListener {
            mActivity.supportFragmentManager?.let{fragmentManager ->
                val dialog: WithdrawalDialogFragment = WithdrawalDialogFragment()
                if (null == fragmentManager.findFragmentByTag(AppTag.WITHDRAWALDIALOG_TAG)) {
                    dialog.show(fragmentManager, "CustomDialog")
                }
            }
        }
    }

    fun setUserLocation(location:String) {
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
        userInfo.location = location
        usersettingViewModel.setUserLocation(location)
    }

    fun setUserProfile(url:String){
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
        userInfo.profileImg = url
    }

    private fun setView(){
        Log.d("실행", "onRequestPermissionsResult() _ 권한 허용")
        binding.textNickname.setText(userInfo.nickName)
        if (userInfo.location == null){
            binding.textLocation.setText("위치 미설정")
        }else
            binding.textLocation.setText(userInfo.location)
            binding.textEmail.setText(userInfo.uid)
        userInfo.profileImg?.let {
            Glide.with(mActivity).load(Uri.parse(it)).apply {
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }.into(binding.profilePhoto)
        }
    }

    fun setUserAccount(account:String){
        userInfo.account = account
    }

    fun setMsgValid(valid: Boolean){
        userInfo.msgVaild = valid
        var sharedPref: SharedPreferences = mActivity.getSharedPreferences(AppTag.USER_INFO, 0)
        var editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean("msgValid", valid).apply()
    }
}