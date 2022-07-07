package kr.co.ajjulcoding.team.project.holo.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.storage.FirebaseStorage
import kr.co.ajjulcoding.team.project.holo.*
import kr.co.ajjulcoding.team.project.holo.base.BaseFragment
import kr.co.ajjulcoding.team.project.holo.data.ChatRoom
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.data.SimpleChatRoom
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentChatListBinding
import kr.co.ajjulcoding.team.project.holo.databinding.ItemChatListRecyclerBinding
import kr.co.ajjulcoding.team.project.holo.view.activity.ChatRoomActivity
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity
import kr.co.ajjulcoding.team.project.holo.view.viewmodel.ChatListViewModel

class ChatListFragment() : BaseFragment<FragmentChatListBinding>() {
    private lateinit var _userInfo: HoloUser
    private val userInfo get() = _userInfo
    private val chatListViewModel: ChatListViewModel by viewModels<ChatListViewModel>()
    private lateinit var signatureKey:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
        ChatRoomActivity.randomNum = null
        chatListViewModel.getUserChatRoomLi(userInfo.uid)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentChatListBinding {
        return FragmentChatListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = mActivity.getSharedPreferences(AppTag.USER_INFO,0)
        signatureKey = sharedPref.getString("signature",System.currentTimeMillis().toString())!!

        binding.recyclerChatList.adapter = ChatListAdapter()
        chatListViewModel.userChatRoomLi.observe(viewLifecycleOwner){
            (binding.recyclerChatList.adapter as ChatListAdapter).replaceItems(it)
        }
    }

    companion object{
        val chatRoomDiffUtil = object : DiffUtil.ItemCallback<ChatRoom>() {
            override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                return oldItem.latestTime == newItem.latestTime
            }

            override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
                return oldItem == newItem && oldItem.latestTime == newItem.latestTime
            }

        }
    }
    inner class ChatListAdapter():
           ListAdapter<ChatRoom, ChatListAdapter.ViewHolder>(chatRoomDiffUtil){
        val FBstorage = FirebaseStorage.getInstance()
        val FBstorageRef = FBstorage.reference
        lateinit var view:ItemChatListRecyclerBinding

        inner class ViewHolder(view: ItemChatListRecyclerBinding):RecyclerView.ViewHolder(view.root){
            val imgProfile = view.circleImageView
            val textMSG = view.textPreMSG
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            view = DataBindingUtil.inflate<ItemChatListRecyclerBinding>(layoutInflater, R.layout.item_chat_list_recycler,parent,false)
            return ViewHolder(view)
        }

        @SuppressLint("CheckResult")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //Log.d("옵저버 데이터 확인1", asyncDiffer.currentList[position].toString())
            currentList!!.get(position).let { item ->
                with(holder){
                    view.chat = item
                    view.user = userInfo
                    Log.d("채팅 데이터 출력", item.snickName.toString()+" "+item.rnickName)
                    Log.d("채채팅 데이터 출력", userInfo.nickName.toString())
                    var fileName = "profile_"
                    //var nickName:String
                    if (item.remail == userInfo.uid){
                        fileName += item.semail.replace(".", "") + ".jpg"
                        view.nickName = item.snickName
                    }else{
                        fileName += item.remail.replace(".", "") + ".jpg"
                        view.nickName = item.rnickName
                    }
                    item.talkContent.let {
                        if (it.size >0){
                            textMSG.setText(it[it.size-1].content)
                        }
                    }

                    val mountainRef = FBstorageRef.child("profile_img/"+fileName)
                    val requestOptions = RequestOptions()
                    requestOptions.apply {
                        signature(ObjectKey(signatureKey))
                    }
                    Glide.with(this@ChatListFragment).load(mountainRef)
                        .apply {
                            placeholder(R.drawable.background_profile)
                            thumbnail(0.1f)
                            apply(requestOptions)
                            into(imgProfile)
                        }

                    itemView.setOnClickListener {
                        val intentChatRoom = Intent(mActivity, ChatRoomActivity::class.java)
                        SettingInApp.uniqueActivity(intentChatRoom)
                        val scrData = SimpleChatRoom(item.title,item.participant, item.randomDouble,item.semail,
                        item.snickName,item.stoken,item.remail,item.rnickName,item.rtoken)
                        intentChatRoom.putExtra(AppTag.USER_INFO, userInfo)
                        intentChatRoom.putExtra(AppTag.CHATROOM_TAG, scrData)
                        startActivity(intentChatRoom)
                    }
                }
            }
        }

        override fun getItemCount() = currentList.size

        fun replaceItems(newItemLi: ArrayList<ChatRoom>){
            submitList(newItemLi.toMutableList())   // 사본 생성: 객체 달라짐
        }

    }
}