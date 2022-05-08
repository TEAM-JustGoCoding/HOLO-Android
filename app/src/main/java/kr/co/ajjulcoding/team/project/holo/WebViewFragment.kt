package kr.co.ajjulcoding.team.project.holo

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentWebViewBinding

class WebViewFragment : Fragment() {
    private lateinit var _binding: FragmentWebViewBinding
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
        val webView:WebView = binding.webView
        webView.apply {
            val webViewClient = webViewClient
            settings.javaScriptEnabled = true
        }
        webView.loadUrl("https://github.com/YeeunLee8245/MyWriting-AndroidApp")
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


}