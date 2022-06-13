package kr.co.ajjulcoding.team.project.holo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentNotificationBinding

class NotificationFragment(var currentUser:HoloUser) : Fragment(), OnItemClick {
    private lateinit var _binding: FragmentNotificationBinding
    private val binding get() = _binding
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private var mRecyclerView: RecyclerView? = null
    private var mRecyclerAdapter: NotificationAdapter? = null
    private var mNotificationItems: ArrayList<NotificationItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        initList()
        Log.d("알림 프래그먼트", "onCreateView")
        return binding.root
    }

    private fun initList(){
        mNotificationItems = currentUser.notificationlist
        Log.d("알림 initList", mNotificationItems.toString())

        mRecyclerAdapter = NotificationAdapter(_activity, this)

        if (mNotificationItems == null){
            mNotificationItems=ArrayList()
        }
        else {
            mNotificationItems = mActivity.getNotificationJSON()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("알림 프래그먼트", "onViewCreated")

        mRecyclerView = requireView().findViewById(R.id.recyclerView) as RecyclerView?

        /* initiate adapter */
//        mRecyclerAdapter = NotificationAdapter(this)

        /* initiate recyclerview */
        mRecyclerView!!.adapter = mRecyclerAdapter
//        mRecyclerView!!.layoutManager = LinearLayoutManager(requireContext())
//        mRecyclerView!!.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

//        /* adapt data */
//        mNotificationItems = ArrayList()
//        for (i in 1..10) {
//            if (i % 2 == 0) mNotificationItems!!.add(
//                NotificationItem(
//                    "작성한 게시글에 댓글이 달렸습니다",
//                    "저도 메가커피 주문하고 싶어요!"
//                )
//            ) else mNotificationItems!!.add(
//                NotificationItem(
//                    "작성한 댓글에 답글이 달렸습니다",
//                    "8000원 정도 주문할 예정입니다!"
//                )
//            )
//        }
        mRecyclerAdapter!!.setNotificationList(mNotificationItems)

        binding.btnBack.setOnClickListener {
            mActivity.changeFragment(AppTag.HOME_TAG)
        }
    }

    override fun onClikDelete(position: Int?) {
        mNotificationItems!!.removeAt(position!!)
        mRecyclerAdapter!!.setNotificationList(mNotificationItems)
    }

}