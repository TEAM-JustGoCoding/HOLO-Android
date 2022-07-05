package kr.co.ajjulcoding.team.project.holo.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.util.OnItemClick
import kr.co.ajjulcoding.team.project.holo.R
import kr.co.ajjulcoding.team.project.holo.data.UtilityBillItem


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

    fun getItemContent(position: Int?): String? {
        return mUitilityBillList!!.get(position!!).getContent()
    }

    fun getItemTerm(position: Int?): Int? {
        return mUitilityBillList!!.get(position!!).getTerm()
    }

    fun getItemDay(position: Int?): Int? {
        return mUitilityBillList!!.get(position!!).getDay()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val delBtn: Button
        var content: EditText
        var term = 0
        var day = 1
        var termSpin: Spinner
        var dateSpin: Spinner
        fun onBind(item: UtilityBillItem) {
            content.setText(item.getContent())
            content.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View, hasFocus: Boolean) {
                    if (hasFocus) {
                        item.setContent(content.text.toString())
                    }
                    else {
                        item.setContent(content.text.toString())
                    }
                }
            })
            val mTermArrayAdapter = ArrayAdapter.createFromResource(itemView.getContext(),
                R.array.term_array,
                R.layout.spinner_list
            )
            mTermArrayAdapter.setDropDownViewResource(R.layout.spinner_list)
            val mDateArrayAdapter = ArrayAdapter.createFromResource(itemView.getContext(),
                R.array.date_array,
                R.layout.spinner_list
            )
            mDateArrayAdapter.setDropDownViewResource(R.layout.spinner_list)
            termSpin.adapter = mTermArrayAdapter
            dateSpin.adapter = mDateArrayAdapter
            termSpin.setEnabled(true)
            dateSpin.setEnabled(true)
            termSpin.setSelection(getItemTerm(position)!!)
            dateSpin.setSelection(getItemDay(position)!!-1)
            termSpin.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    item.setTerm(position)
                    term = position
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    item.setTerm(term)
                }
            }
            dateSpin.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    item.setDay(position+1)
                    day = position+1
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    item.setDay(day)
                }
            }
            delBtn.setOnClickListener {
                onItemClick.onClikDelete(position)
                Log.d("공과금 어댑터 position", position.toString())
            }
        }

        init {
            content = itemView.findViewById(R.id.content) as EditText
            termSpin = itemView.findViewById(R.id.TermSpinner) as Spinner
            dateSpin = itemView.findViewById(R.id.DateSpinner) as Spinner
            delBtn = itemView.findViewById(R.id.BtnDelete) as Button
        }
    }
}

