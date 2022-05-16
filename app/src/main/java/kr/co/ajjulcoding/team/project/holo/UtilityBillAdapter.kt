package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView


class UtilityBillAdapter(val onItemClick: OnItemClick) : RecyclerView.Adapter<UtilityBillAdapter.ViewHolder>() {
    private var mUitilityBillList: ArrayList<UtilityBillItem>? = null
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

    fun getItemTerm(position: Int?): Int? {
        return mUitilityBillList!!.get(position!!).getTerm()
    }

    fun getItemDay(position: Int?): Int? {
        return mUitilityBillList!!.get(position!!).getDay()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delBtn: Button
        var content: TextView
        var term = 0
        var day = 0
        var termSpin: Spinner
        var dateSpin: Spinner
        fun onBind(item: UtilityBillItem) {
            content.setText(item.getContent())
            termSpin.adapter = ArrayAdapter.createFromResource(itemView.getContext(), R.array.term_array, android.R.layout.simple_spinner_item)
            dateSpin.adapter = ArrayAdapter.createFromResource(itemView.getContext(), R.array.date_array, android.R.layout.simple_spinner_item)
            termSpin.setEnabled(true)
            dateSpin.setEnabled(true)
            termSpin.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    item.setTerm(position)
                    term = position
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    term = 1
                }
            }
            dateSpin.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    item.setDay(position)
                    day = position
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    day = 1
                }
            }
            delBtn.setOnClickListener {
                onItemClick.onClikDelete(position)
                Log.d("공과금 프래그먼트 position", position.toString())
            }
        }

        init {
            content = itemView.findViewById(R.id.content) as TextView
            termSpin = itemView.findViewById(R.id.TermSpinner) as Spinner
            dateSpin = itemView.findViewById(R.id.DateSpinner) as Spinner
            delBtn = itemView.findViewById(R.id.BtnDelete) as Button
        }
    }
}

