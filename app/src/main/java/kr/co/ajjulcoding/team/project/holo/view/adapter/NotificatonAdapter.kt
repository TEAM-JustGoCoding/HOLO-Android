package kr.co.ajjulcoding.team.project.holo.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.data.NotificationItem
import kr.co.ajjulcoding.team.project.holo.util.OnItemClick
import kr.co.ajjulcoding.team.project.holo.R
import kr.co.ajjulcoding.team.project.holo.view.activity.MainActivity


class NotificationAdapter(val mActivity: MainActivity, onItemClick: OnItemClick) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private var mNotificationList: ArrayList<NotificationItem>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.notification_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mNotificationList!![position])
    }

    fun setNotificationList(list: ArrayList<NotificationItem>?) {
        mNotificationList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mNotificationList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var message: TextView
        fun onBind(item: NotificationItem) {
            name.setText(item.getName())
            message.setText(item.getMessage())
            itemView.setOnClickListener(){
                mActivity.changeFragment(item.getURL().toString())
            }
        }

        init {
            name = itemView.findViewById<View>(R.id.name) as TextView
            message = itemView.findViewById<View>(R.id.message) as TextView
        }
    }
}