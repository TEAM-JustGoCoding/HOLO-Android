package kr.co.ajjulcoding.team.project.holo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityChatRoomBinding

class ChatRoomActivity() : AppCompatActivity() {
    private lateinit var userEmail:String
    private lateinit var userInfo:HoloUser
    private lateinit var chatRoomData:SimpleChatRoom
    private lateinit var _binding:ActivityChatRoomBinding
    private lateinit var chatRoomViewModel: ChatRoomViewModel
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatRoomViewModel = ViewModelProvider(this).get(ChatRoomViewModel::class.java)
        userInfo = intent.getParcelableExtra<HoloUser>(AppTag.USER_INFO)!!
        userEmail = userInfo.uid
        chatRoomData = intent.getParcelableExtra<SimpleChatRoom>(AppTag.CHATROOM_TAG)!!
        _binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewData()
        binding.btnSendText.setOnClickListener {
            Log.d("네트워크 확인", checkNetwork().toString())
            if (!checkNetwork()){   // 네트워크 X
                showAlertDialog("네트워크 연결을 확인할 수 없습니다!", *arrayOf("확인"))
                return@setOnClickListener
            }

            sendMSG()   // 채팅 전송
        }
        binding.btnSubmit.setOnClickListener {
            if (!checkNetwork()){   // 네트워크 X
                showAlertDialog("네트워크 연결을 확인할 수 없습니다!", *arrayOf("확인"))
                return@setOnClickListener
            }

            sendStar()   // 별점 전송
        }
        // TODO: 리사이클러뷰 스크롤 아래 정렬
    }

    private fun setViewData(){
        if (chatRoomData.semail == userEmail){  // 사용자는 semail
            binding.txtNickName.setText(chatRoomData.snickName)
        }else
            binding.txtNickName.setText(chatRoomData.rnickName)

        binding.txtTitle.setText(chatRoomData.title)
    }

    private fun checkNetwork(): Boolean{
        val conManager = getSystemService(ConnectivityManager::class.java)
        val currentNet = conManager.activeNetwork ?: return false
        val actNet = conManager.getNetworkCapabilities(currentNet) ?: return false

        return when {
            actNet.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNet.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
        }
    }

    private fun showAlertDialog(msg:String, vararg option:String){
        AlertDialog.Builder(this)
            .setTitle(msg)
            .setCancelable(false)
            .setItems(option, object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface, idx: Int) {
                    dialog.dismiss()
                }
            })
            .create().show()
    }

    private fun sendMSG(){
        val content = binding.editChat.text.toString()
        chatRoomViewModel.setChatBubble(userInfo, chatRoomData, content)
        binding.editChat.setText("")
        // 키보드 내리고 포커스 아웃
        val imm =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editChat.windowToken, 0)
        binding.editChat.clearFocus()
    }

    private fun sendStar(){}

    // TODO: 채팅 실시간 보내서 UI 출력
    //inner class ChatRoomAdapter():
}