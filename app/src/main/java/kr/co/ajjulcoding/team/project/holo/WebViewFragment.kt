package kr.co.ajjulcoding.team.project.holo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentWebViewBinding

class WebViewFragment(private val webUrl: String) : Fragment() {
    private lateinit var _binding: FragmentWebViewBinding
    private lateinit var webViewModel: WebViewModel
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
        _binding = FragmentWebViewBinding.inflate(inflater, container, false)
        webViewModel = ViewModelProvider(this).get(WebViewModel::class.java)
        val webView:WebView = binding.webView
        webView.apply {
            val webViewClient = webViewClient
            settings.javaScriptEnabled = true
        }
        webView.loadUrl(webUrl)//("https://github.com/YeeunLee8245/MyWriting-AndroidApp")
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
    class WebAppInterface(private val mContext: Context) {
        @JavascriptInterface
        fun showToast(toast: String){
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendCmtAlarm(email: String, msg: String, content: String){  // TODO: 예은 님이 댓글/답글을 남겼습니다., (내용)
        // TODO: 이메일로 상대방 토큰 받아오기
        //val diff
//        val data = CmtNotificationBody.CmtNotificationData(msg, content)
//        val body = CmtNotificationBody()

    }
}