package kr.co.ajjulcoding.team.project.holo

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentHomeBinding
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent


class HomeFragment(val currentUser:HoloUser) : Fragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private lateinit var _binding:FragmentHomeBinding
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels<HomeViewModel>()
    private var mNotificationItems: ArrayList<NotificationItem>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) // android 30 부터
//            mActivity.window.setDecorFitsSystemWindows(false)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setProfile()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    Toast.makeText(mActivity,"검색어를 입력해주세요!", Toast.LENGTH_SHORT).show()
                else {
                    val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // 키보드 내리기
                    imm.hideSoftInputFromWindow(binding.editSearch.windowToken,0)
                    mActivity.changeFragment(WebUrl.URL_LAN + WebUrl.URL_SEARCH + text)  // TODO: 아직 url 설정 안 한 상태, webview에서 검색어 분리 필요
                }
            }

            true
        }
        binding.btnSell.setOnClickListener {
            if (currentUser.location == null) {
                mActivity.showAlertDialog("프로필 > 내 동네 설정을 통해 나의 위치를 설정해주세요!", *arrayOf("확인"))
                return@setOnClickListener
            }
            currentUser.location?.let {
                mActivity.changeFragment(WebUrl.URL_LAN+WebUrl.URL_DEAL)
            }
        }
        binding.btnWatch.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_LAN+WebUrl.URL_OTT)
        }
        binding.btnSight.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_LAN+WebUrl.URL_DCM)
        }
        binding.btnPolicy.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_LAN+WebUrl.URL_POLICY)
        }
        binding.btnCuriosity.setOnClickListener {
            mActivity.changeFragment(WebUrl.URL_LAN+WebUrl.URL_FAQ)
        }
        binding.btnNotifi.setOnClickListener {
            mActivity.changeFragment(AppTag.NOTIFICATION_TAG)
        }

        // TODO: 알림 테스트(자기 자신에게 알림 옴)
        binding.textLocation.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val repository = Repository()
                val rNicknameAndToken: Deferred<Pair<String,String>> = homeViewModel.getUserNicknameAndToken(currentUser.uid)
                val receiverNickname = rNicknameAndToken.await().first
                val receiverToken = rNicknameAndToken.await().second
                var content = "이 집 많이 매워요??"
                var msg = receiverNickname+" 님이 댓글을 남겼습니다"
                var url = WebUrl.URL_LAN+WebUrl.URL_POLICY
                val data = CmtNotificationBody.CmtNotificationData(msg, content, url)
                val body = CmtNotificationBody(receiverToken, data)
                repository.sendCmtPushAlarm(body)
                //알림 구현 틀
                mNotificationItems = currentUser.notificationlist
                if (mNotificationItems==null)
                    mNotificationItems=ArrayList()
                mNotificationItems!!.add(NotificationItem(msg, content, url))
                mActivity.storeNotificationCache(mNotificationItems!!)
            }
        }
    }

    private fun setProfile(){
        binding.textProfileName.setText(currentUser.nickName)
        if (currentUser.location == null){
            binding.textLocation.setText("위치 등록")
        }else
            binding.textLocation.setText(currentUser.location)
        currentUser.profileImg?.let {
            Glide.with(_activity).load(Uri.parse(it)).apply {
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }.into(binding.circleImageView)
        }
    }
}