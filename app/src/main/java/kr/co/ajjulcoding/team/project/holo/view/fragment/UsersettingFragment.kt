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
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentUsersettingBinding
import kr.co.ajjulcoding.team.project.holo.util.ToastUtil
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity
import kr.co.ajjulcoding.team.project.holo.view.viewmodel.UsersettingViewModel


class UsersettingFragment() : Fragment() {
    private lateinit var _activity: MainActivity
    private val mActivity get() = _activity
    private lateinit var _userInfo: HoloUser
    private val userInfo get() = _userInfo
    private lateinit var _binding: FragmentUsersettingBinding
    private val binding get() = _binding
    private val usersettingViewModel: UsersettingViewModel by viewModels<UsersettingViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsersettingBinding.inflate(inflater, container, false)
        setView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: ??????????????? ????????? ???
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
                .setTitle("???????????? ???????????????????")
                .setItems(arrayOf("???","?????????"), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, idx: Int) {
                        dialog!!.dismiss()
                        if (idx == 0){
                            SettingInApp.mAuth.signOut()
                            ToastUtil.showToast(requireActivity(), "???????????? ???????????????.")
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
        Log.d("??????", "onRequestPermissionsResult() _ ?????? ??????")
        binding.textNickname.setText(userInfo.nickName)
        if (userInfo.location == null){
            binding.textLocation.setText("?????? ?????????")
        }else
            binding.textLocation.setText(userInfo.location)
            binding.textEmail.setText(userInfo.uid)
        userInfo.profileImg?.let {
            Glide.with(_activity).load(Uri.parse(it)).apply {
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