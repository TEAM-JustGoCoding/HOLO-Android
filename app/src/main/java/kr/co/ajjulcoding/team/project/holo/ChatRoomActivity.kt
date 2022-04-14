package kr.co.ajjulcoding.team.project.holo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityChatRoomBinding

class ChatRoomActivity() : AppCompatActivity() {
    private lateinit var userEmail:String
    private lateinit var chatRoomData:SimpleChatRoom
    private lateinit var _binding:ActivityChatRoomBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userEmail = FirebaseAuth.getInstance().currentUser!!.email.toString()
        chatRoomData = intent.getParcelableExtra<SimpleChatRoom>(AppTag.CHATROOM_TAG)!!
        _binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewData()
        // TODO: 리사이클러뷰 스크롤 아래 정렬
    }

    private fun setViewData(){
        if (chatRoomData.semail == userEmail){  // 사용자는 semail
            binding.txtNickName.setText(chatRoomData.snickName)
        }else
            binding.txtNickName.setText(chatRoomData.rnickName)

        binding.txtTitle.setText(chatRoomData.title)
    }

    //inner class ChatRoomAdapter():
}