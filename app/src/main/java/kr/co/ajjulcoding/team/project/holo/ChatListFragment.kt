package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentChatListBinding
import kr.co.ajjulcoding.team.project.holo.databinding.ItemChatListRecyclerBinding
import java.io.File

class ChatListFragment(val userInfo:HoloUser) : Fragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private lateinit var _binding: FragmentChatListBinding
    private val binding get() = _binding
    private val chatListViewModel: ChatListViewModel by viewModels<ChatListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
        chatListViewModel.getUserChatRoomLi(userInfo.uid)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //val chatListAdapter:ChatListAdapter = ChatListAdapter(chatListViewModel.userChatRoomLi.value!!)
        binding.recyclerChatList.adapter = ChatListAdapter()
        chatListViewModel.userChatRoomLi.observe(viewLifecycleOwner){
            (binding.recyclerChatList.adapter as ChatListAdapter).replaceItems(it)
        }
    }

    private class CRoomAsyncDifCallback : DiffUtil.ItemCallback<ChatRoom>(){
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean{
            Log.d("옵저버 데이터 확인2", oldItem.latestTime.toString()+" "+newItem.latestTime.toString())
            return oldItem.latestTime == newItem.latestTime
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom):Boolean {
            Log.d("옵저버 데이터 확인3", oldItem.toString()+"다름 "+newItem.toString())
            return oldItem == newItem && oldItem.latestTime == newItem.latestTime
        }


    }
    companion object{
        val chatRoomDifUtil = object : DiffUtil.ItemCallback<ChatRoom>() {
            override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                Log.d("옵저버 데이터 확인2", oldItem.latestTime.toString()+" "+newItem.latestTime.toString())
                return oldItem.latestTime == newItem.latestTime
            }

            override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                Log.d("옵저버 데이터 확인3", oldItem.toString()+"다름 "+newItem.toString())
                return oldItem == newItem && oldItem.latestTime == newItem.latestTime
            }

        }
    }
    inner class ChatListAdapter():
           ListAdapter<ChatRoom, ChatListAdapter.ViewHolder>(chatRoomDifUtil){
        val FBstorage = FirebaseStorage.getInstance()
        val FBstorageRef = FBstorage.reference

        inner class ViewHolder(view: ItemChatListRecyclerBinding):RecyclerView.ViewHolder(view.root){
            val imgProfile = view.circleImageView
            val textNickName = view.textNickName
            val textTitle = view.textTitle
            val textMSG = view.textPreMSG
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = ItemChatListRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //Log.d("옵저버 데이터 확인1", asyncDiffer.currentList[position].toString())
            currentList!!.get(position).let { item ->
                with(holder){
                    var fileName = "profile_"
                    var nickName:String
                    Log.d("아이디 이메일 확인", item.remail+"  "+userInfo.uid)
                    if (item.remail == userInfo.uid){
                        fileName += item.semail.replace(".", "") + ".jpg"
                        nickName = item.snickName
                    }else{
                        fileName += item.remail.replace(".", "") + ".jpg"
                        nickName = item.rnickName
                    }
                    textNickName.setText(nickName)
                    textTitle.setText(item.title)
                    item.talkContent.let {
                        if (it.size >0){
                            textMSG.setText(it[it.size-1])
                        }
                    }
                    val mountainRef = FBstorageRef.child("profile_img/"+fileName)
                    GlideApp.with(this@ChatListFragment).load(mountainRef)
                        .thumbnail(0.1f)
                        .into(imgProfile)
                    itemView.setOnClickListener {
                        val intentChatRoom = Intent(mActivity, ChatRoomActivity::class.java)
                        SettingInApp.uniqueActivity(intentChatRoom)
                        val scrData = SimpleChatRoom(item.title,item.participant,item.latestTime,item.semail,
                        item.snickName,item.stoken,item.remail,item.rnickName,item.rtoken)
                        Log.d("채팅방 입장 데이터", scrData.toString())
                        intentChatRoom.putExtra(AppTag.CHATROOM_TAG, scrData)
                        startActivity(intentChatRoom)
                    }
                }
                // TODO: 터치하면 채팅방 액티배티 나오게 클릭 리스너 추가하기
            }
        }

        override fun getItemCount() = currentList.size

        fun replaceItems(newItemLi: ArrayList<ChatRoom>){
            submitList(newItemLi.toMutableList())   // 사본 생성: 객체 달라짐
        }

    }
}