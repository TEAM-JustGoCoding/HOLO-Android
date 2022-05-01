package kr.co.ajjulcoding.team.project.holo

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityChatRoomBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        chatRoomViewModel.getChatBubbleLi(chatRoomData.title, chatRoomData.randomDouble!!)
        binding.recyclerBubble.adapter = ChatRoomAdapter()
        val bubbleObserver = object : Observer<ArrayList<ChatBubble>>{
            override fun onChanged(bubbleLi: ArrayList<ChatBubble>?) {
                Log.d("채팅방 옵저버 확인", bubbleLi.toString())
                (binding.recyclerBubble.adapter as ChatRoomAdapter).replaceBubbles(bubbleLi!!)
            }
        }
        binding.recyclerBubble.adapter!!.registerAdapterDataObserver(ChatDataObserver())

        chatRoomViewModel.chatBubbleLi.observe(this, bubbleObserver)
        chatRoomViewModel.validScore.observe(this){
            Log.d("채팅방 삭제 시작","ㅇㅇ")
            // TODO: 채팅방 삭제하기
        }
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
            val numStar:Float = binding.ratingBar.rating // 0.1f
            if (numStar == 0f){
                showAlertDialog("별점을 드래그하여 점수를 설정해주세요!", *arrayOf("확인"))
                return@setOnClickListener
            }
            chatRoomViewModel.postScore(userInfo.uid, numStar)   // 별점 전송
        }
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
    }

    companion object{
        const val LEFT_BUBBLE = 1
        const val RIGHT_BUBBLE = 2
        val chatBubbleDiffUtil = object : DiffUtil.ItemCallback<ChatBubble>(){
            override fun areItemsTheSame(oldItem: ChatBubble, newItem: ChatBubble): Boolean {
                Log.d("옵저버버 데이터 확인4", oldItem.currentTime.toString()+" "+newItem.currentTime.toString())
                return oldItem.currentTime == newItem.currentTime
            }

            override fun areContentsTheSame(oldItem: ChatBubble, newItem: ChatBubble): Boolean {
                Log.d("옵저버 데이터 확인5", oldItem.toString()+"다름 "+newItem.toString())
                return oldItem == newItem && oldItem.currentTime == newItem.currentTime
            }

        }
    }
    inner class ChatDataObserver(): RecyclerView.AdapterDataObserver(){
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            binding.recyclerBubble.scrollToPosition(0)
            super.onItemRangeInserted(positionStart, itemCount)
        }
    }
    // TODO: 채팅 실시간 보내서 UI 출력
    inner class ChatRoomAdapter(): ListAdapter<ChatBubble, RecyclerView.ViewHolder>(chatBubbleDiffUtil){

        inner class LeftViewHolder(view: View): RecyclerView.ViewHolder(view){
            private val textChat: TextView = view.findViewById(R.id.txtLeftChat)
            private val textDate: TextView = view.findViewById(R.id.txtLeftDate)

            fun bind(item: ChatBubble){
                val time:Timestamp = item.currentTime!!
                val millisec:Long = time.seconds * 1000 + time.nanoseconds / 1000000
                val sdf = SimpleDateFormat("E요일, kk:mm", Locale.KOREA)
                val tmpDate = Date(millisec)
                val date = sdf.format(tmpDate)
                textDate.setText(date)
                textChat.setText(item.content)
            }
        }
        inner class RightViewHolder(view: View): RecyclerView.ViewHolder(view){
            private val textChat: TextView = view.findViewById(R.id.txtRightChat)
            private val textDate: TextView = view.findViewById(R.id.txtRightDate)

            fun bind(item: ChatBubble){
                val time:Timestamp = item.currentTime!!
                val millisec:Long = time.seconds * 1000 + time.nanoseconds / 1000000
                val sdf = SimpleDateFormat("E요일, kk:mm", Locale.KOREA)
                val tmpDate = Date(millisec)
                val date = sdf.format(tmpDate)
                textDate.setText(date)
                textChat.setText(item.content)
            }
        }
        override fun getItemViewType(position: Int): Int {
            return currentList!!.get(position).let { bubble ->
                if (bubble.nickname == userInfo.nickName)
                    RIGHT_BUBBLE
                else
                    LEFT_BUBBLE
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View?
            return when (viewType) {
                LEFT_BUBBLE -> {
                    view = LayoutInflater.from(parent.context).inflate(
                        R.layout.item_chat_left_recycler,
                        parent,
                        false)
                    LeftViewHolder(view)
                }

                else -> {
                    view = LayoutInflater.from(parent.context).inflate(
                        R.layout.item_chat_right_recycler,
                        parent,
                        false)
                    RightViewHolder(view)
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            currentList!!.get(position).let { bubble ->
                var bubbleType:Int = LEFT_BUBBLE
                if (bubble.nickname == userInfo.nickName)
                    bubbleType = RIGHT_BUBBLE
                when (bubbleType){
                    LEFT_BUBBLE -> {
                        (holder as LeftViewHolder).bind(bubble)
                    }
                    else -> {
                        (holder as RightViewHolder).bind(bubble)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return currentList.size
        }

        fun replaceBubbles(newBubbleLi: ArrayList<ChatBubble>){
            submitList(newBubbleLi)
        }

    }
}