package kr.co.ajjulcoding.team.project.holo.view.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kr.co.ajjulcoding.team.project.holo.*
import kr.co.ajjulcoding.team.project.holo.base.BaseFragment
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentHomeBinding
import kr.co.ajjulcoding.team.project.holo.util.ToastUtil


class HomeFragment() : BaseFragment<FragmentHomeBinding>() {
    private lateinit var _currentUser: HoloUser
    private val currentUser get() = _currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _currentUser = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProfile()
        Log.d("데이터 확인", currentUser.toString())
//        binding.btnSell.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
//                Log.d("채팅방 열기 버튼 클릭", "들어옴")
//                val receiverEmail = "skqhddl7@gmail.com"
//                val rNicknameAndToken = homeViewModel.getUserNicknameAndToken(receiverEmail)
//                val receiverNickname = rNicknameAndToken.await().first
//                val receiverToken = rNicknameAndToken.await().second
//                val chatRoomData = ChatRoom("오늘밤에 서브웨이 같이 드실 분!", arrayListOf(currentUser.uid, receiverEmail)
//                    , currentUser.uid!!, currentUser.nickName
//                    ,currentUser.token ?: "토큰캐시없음", receiverEmail, receiverNickname, receiverToken
//                    , Timestamp.now())
//                val valid:Deferred<Boolean> = homeViewModel.createChatRoom(chatRoomData, mActivity)
//                if (!(valid.await())){
//                    mActivity.showAlertDialog("네트워크 연결을 확인할 수 없습니다!", *arrayOf("확인"))
//                    return@launch
//                }else
//                    mActivity.showAlertDialog("채팅방이 개설되었습니다!", *arrayOf("확인"))
//            }
//        }
        binding.editSearch.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                val text: String = binding.editSearch.text.toString()
                if (text == "")
                    ToastUtil.showToast(mActivity,"검색어를 입력해주세요!")
                else if (text == "거의동" || text == "옥계동"){   // TODO: 위치정보 테스트용
                    currentUser.location = text
                } else {
                    val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // 키보드 내리기
                    imm.hideSoftInputFromWindow(binding.editSearch.windowToken,0)
                    mActivity.changeFragment(WebUrl.URL_BASE + WebUrl.URL_SEARCH + text)
                    binding.editSearch.setText("")
                }
                return@setOnKeyListener true
            }

            false
        }
        //binding.editSearch.setkey
        binding.btnSell.setOnClickListener {
            if (currentUser.location == null) {
                mActivity.showAlertDialog("프로필 > 내 동네 설정을 통해 나의 위치를 설정해주세요!", *arrayOf("확인"))
                return@setOnClickListener
            }
            currentUser.location?.let {
                mActivity.changeFragment(WebUrl.URL_BASE + WebUrl.URL_DEAL)
            }
        }
        binding.btnWatch.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_BASE + WebUrl.URL_OTT)
        }
        binding.btnSight.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_BASE + WebUrl.URL_DCM)
        }
        binding.btnPolicy.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_BASE + WebUrl.URL_POLICY)
        }
        binding.btnCuriosity.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_BASE + WebUrl.URL_FAQ)
        }
        binding.btnNotifi.setOnClickListener {
            mActivity.changeFragment(AppTag.NOTIFICATION_TAG)
        }

    }

    private fun setProfile(){
        binding.textProfileName.setText(currentUser.nickName)
        if (currentUser.location == null){
            binding.textLocation.setText("위치 등록")
        }else
            binding.textLocation.setText(currentUser.location)
        currentUser.profileImg?.let {
            Glide.with(mActivity).load(Uri.parse(it)).apply {
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }.into(binding.circleImageView)
        }
    }
}