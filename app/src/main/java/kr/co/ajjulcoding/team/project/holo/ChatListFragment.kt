package kr.co.ajjulcoding.team.project.holo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentChatListBinding
import kr.co.ajjulcoding.team.project.holo.databinding.ItemChatListBinding

class ChatListFragment(val userInfo:HoloUser) : Fragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private lateinit var _binding: FragmentChatListBinding
    private val binding get() = _binding
    private val chatListViewModel: ChatListViewModel by viewModels<ChatListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        chatListViewModel.userChatRoomLi.observe(viewLifecycleOwner){
            // TODO: 내일 여기서부터 시작
        }
    }
    // TODO: 아이템 만들고 시작하기
//    inner class ChatListAdapter(private var itemLi: ArrayList<ChatRoom>):
//            RecyclerView.Adapter<ChatListAdapter.ViewHolder>(){
//
//                inner class ViewHolder(view: ItemChatListBinding)
//
//            }
}