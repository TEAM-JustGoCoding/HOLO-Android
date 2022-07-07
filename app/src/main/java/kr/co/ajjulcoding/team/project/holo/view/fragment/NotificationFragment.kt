package kr.co.ajjulcoding.team.project.holo.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.*
import kr.co.ajjulcoding.team.project.holo.base.BaseFragment
import kr.co.ajjulcoding.team.project.holo.data.HoloUser
import kr.co.ajjulcoding.team.project.holo.data.NotificationItem
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentNotificationBinding
import kr.co.ajjulcoding.team.project.holo.util.OnItemClick
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity
import kr.co.ajjulcoding.team.project.holo.view.adapter.NotificationAdapter

class NotificationFragment() : BaseFragment<FragmentNotificationBinding>(), OnItemClick {
    private lateinit var _userInfo: HoloUser
    private val userInfo get() = _userInfo
    private var mRecyclerView: RecyclerView? = null
    private var mRecyclerAdapter: NotificationAdapter? = null
    private var mNotificationItems: ArrayList<NotificationItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _userInfo = arguments?.getParcelable<HoloUser>(AppTag.USER_INFO) as HoloUser
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNotificationBinding {
        return FragmentNotificationBinding.inflate(inflater, container, false)
    }

    private fun initList(){
        mNotificationItems = userInfo.notificationlist
        Log.d("알림 initList", mNotificationItems.toString())

        mRecyclerAdapter = NotificationAdapter(mActivity, this)

        if (mNotificationItems == null){
            mNotificationItems=ArrayList()
        }
        else {
            mNotificationItems = mActivity.getNotificationJSON()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()

        mRecyclerView = requireView().findViewById(R.id.recyclerView) as RecyclerView?
        mRecyclerView!!.adapter = mRecyclerAdapter
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