package kr.co.ajjulcoding.team.project.holo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.viewModels
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentWebViewBinding
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class WebViewFragment(private val userInfo: HoloUser, private val webUrl: String) : Fragment() {
    companion object{
        const val COMMENT_TAG = "comment"
        const val SUBCOMMENT_TAG = "subComment"
    }
    private lateinit var _binding: FragmentWebViewBinding
    private lateinit var webViewModel: WebViewModel
    private val binding get() = _binding
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private val webVieModel: WebViewModel by viewModels<WebViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        webViewModel = ViewModelProvider(this).get(WebViewModel::class.java)
        CoroutineScope(Dispatchers.Main).launch {
            val resultDef:Deferred<Int?> = webViewModel.getId(userInfo.uid)
            val userId:Int? = resultDef.await()
            Log.d("아이디 받은 거",userId.toString())
            if (userId == null) {
                Toast.makeText(mActivity, "네트워크 통신 오류입니다.", Toast.LENGTH_SHORT).show()
                mActivity.changeFragment(AppTag.HOME_TAG)
            }
            val webView:WebView = binding.webView
            webView.apply {
                //val webViewClient = webViewClient
                settings.javaScriptEnabled = true
                //webChromeClient = WebChromeClient()
                //webViewClient = WebViewClientClass()
                //settings.cacheMode = WebSettings.LOAD_NO_CACHE    // 캐시 튜닝(성능 위해)
                //settings.textZoom = 100 // 글꼴 크기 고정
                addJavascriptInterface(WebAppInterface(mActivity, webView), "Android")
            }
            CookieManager.getInstance().apply {
                removeAllCookies(null)
                setCookie("http://192.168.43.29:3000","uid = ${userId}")
                setCookie("http://192.168.43.29:3000","town = ${userInfo.location}")
                setAcceptThirdPartyCookies(webView,true)
            }

            var postData: String = "uid=" + URLEncoder.encode(userInfo.uid, "UTF-8")
            if (webUrl == (WebUrl.URL_LAN+WebUrl.URL_DEAL)){
                postData += "&town=" + URLEncoder.encode(userInfo.location, "UTF-8")
                Log.d("배달 공구", webUrl.toString())
                webView.loadUrl(webUrl)
                //webView.postUrl(webUrl, postData.encodeToByteArray())
            }else {
                Log.d("그 외", webUrl.toString())
                webView.loadUrl(webUrl)//("https://github.com/YeeunLee8245/MyWriting-AndroidApp")
                //webView.loadUrl("javascript:mobileToReact(${userInfo.uid})")
                //webView.postUrl(webUrl, postData.encodeToByteArray())
            }

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webView:WebView = binding.webView
        webView.setOnKeyListener { view, i, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                if (webView.canGoBack()){
                    webView.goBack()
                }else {
//                    System.exit(0) // 강종
                    mActivity.onBackPressed()
                }
            }
            return@setOnKeyListener true
        }
    }

    inner class WebViewClientClass(): WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view!!.loadUrl(url!!)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            view!!.evaluateJavascript("DocumentPost.mobileToReact(${userInfo.uid})") { v ->
                Log.d("웹에서 받았음",v.toString())
            }
        }
    }


        inner class WebAppInterface(private val mContext: Context, private val webView:WebView){
        @JavascriptInterface
        fun showToast(msg:String){
            Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show()
            //webView.loadUrl("javascript:DocumentPost.mobileToReact(${userInfo.uid})")
            webView.evaluateJavascript("mobileToReact(${userInfo.uid})"){ v ->
                Log.d("웹에서 받았음",v.toString())
                Toast.makeText(mActivity, "웹2", Toast.LENGTH_SHORT).show()
            }
        }

        @JavascriptInterface
        fun createChatRoom(hostEmail: String, partner: String){  // 목표 금액이 달성되면 딱 한 번 호출, {partner: [{email:..], [email:...], ...}]}
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("채팅 json 배열 확인: ", partner)
                val emailJsonArr:JSONArray = JSONObject(partner).optJSONArray("partner")!!
                val hostNicknameAndToken = webVieModel.getUserNicknameAndToken(hostEmail)
                val hostNickname = hostNicknameAndToken.await().first
                val hostToken = hostNicknameAndToken.await().second
                val data = CmtNotificationBody.CmtNotificationData("채팅방이 생성됐습니다!", "거래에 대한 이야기를 나눠보세요!")

                for (i in 0 until emailJsonArr.length()){
                    val jsonObject = emailJsonArr.getJSONObject(i)
                    val receiverEmail = jsonObject.getString("email")
                    Log.d("채팅 json 배열 변환: ", receiverEmail)

                    val rNicknameAndToken: Deferred<Pair<String,String>> = webVieModel.getUserNicknameAndToken(receiverEmail)
                    val receiverNickname = rNicknameAndToken.await().first
                    val receiverToken = rNicknameAndToken.await().second
                    val chatRoomData = ChatRoom("힐스테이트 커피시켜 먹으실 분", arrayListOf(hostEmail, receiverEmail)
                        , hostEmail, hostNickname ,hostToken, receiverEmail, receiverNickname, receiverToken
                        , Timestamp.now())
                    val valid: Deferred<Boolean> = webVieModel.createChatRoom(chatRoomData, mActivity)

                    if (!(valid.await())){
                        mActivity.showAlertDialog("네트워크 연결을 확인할 수 없습니다!", *arrayOf("확인"))
                        return@launch
                    }else
                        mActivity.showAlertDialog("채팅방이 개설되었습니다!", *arrayOf("확인"))

                    // TODO: 알림 생성
                    val body = CmtNotificationBody(receiverToken, data)
                    webViewModel.sendCmtPushAlarm(body)
                }
                val body = CmtNotificationBody(hostToken, data)
                webViewModel.sendCmtPushAlarm(body)
            }
        }

        @JavascriptInterface
        fun sendCmtAlarm(type: String, toEmail: String, content: String){  // TODO: 예은 님이 댓글/답글을 남겼습니다., (내용)
            // TODO: 이메일로 상대방 토큰 받아오기
            CoroutineScope(Dispatchers.IO).launch {
                val deferred: Deferred<Pair<String, String>> = webViewModel.getUserNicknameAndToken(toEmail)
                val defResult: Pair<String, String> = deferred.await()
                var msg: String = userInfo.nickName
                if (type == COMMENT_TAG)
                    msg = "$msg 님이 댓글을 남겼습니다"
                else if (type == SUBCOMMENT_TAG)
                    msg = "$msg 님이 답글을 남겼습니다"
                val data = CmtNotificationBody.CmtNotificationData(msg, content)
                val body = CmtNotificationBody(defResult.second, data)
                webViewModel.sendCmtPushAlarm(body)
            }
        }
    }
}