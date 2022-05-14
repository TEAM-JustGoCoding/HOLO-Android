package kr.co.ajjulcoding.team.project.holo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


//class UtilityBillAdapter(private val items: MutableList<UtilityBillItem>): BaseAdapter() {
//    override fun getCount(): Int = items.size
//    override fun getItem(position: Int): UtilityBillItem = items[position]
//
//    override fun getItemId(position: Int): Long = position.toLong()
//    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
//        var convertView = view
//        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.utilitybill_list_item, parent, false)
//
//        val item: UtilityBillItem = items[position]
//        convertView.content.text = item.content
//
//        return convertView
//    }
//}

class UtilityBillAdapter(items: ArrayList<UtilityBillItem>) : RecyclerView.Adapter<UtilityBillAdapter.ViewHolder>() {
    private var mUitilityBillList: ArrayList<UtilityBillItem>? = items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.utilitybill_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(mUitilityBillList!![position])
    }

    fun setNotificationList(list: ArrayList<UtilityBillItem>?) {
        mUitilityBillList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mUitilityBillList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView
        fun onBind(item: UtilityBillItem) {
            content.setText(item.getContent())
        }

        init {
            content = itemView.findViewById<View>(R.id.content) as TextView
        }
    }
}

