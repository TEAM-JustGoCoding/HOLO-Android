package kr.co.ajjulcoding.team.project.holo


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.GregorianCalendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentUtilityBillBinding


class UtilityBillFragment : DialogFragment(), OnItemClick {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private var _binding: FragmentUtilityBillBinding? = null
    private val binding get() = _binding!!
    private var mlistView: RecyclerView? = null
    private var mUtilityBillAdapter: UtilityBillAdapter? = null
    private var mUtilityBillItems: ArrayList<UtilityBillItem>? = null
    private var mCalender: GregorianCalendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
        mCalender = GregorianCalendar()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUtilityBillBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("공과금 프래그먼트", "onViewCreated")

        mlistView = requireView().findViewById(R.id.listView) as RecyclerView?

        mUtilityBillAdapter = UtilityBillAdapter(this)

        mlistView!!.adapter = mUtilityBillAdapter

        mUtilityBillItems = ArrayList()

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val appendbtn = binding.BtnAppend

        appendbtn.setOnClickListener() {
            mUtilityBillItems!!.add(UtilityBillItem("  ", 0, 0))
            mUtilityBillAdapter!!.notifyDataSetChanged()
        }

        mUtilityBillItems!!.add(UtilityBillItem("월세", 0, 0))
        mUtilityBillItems!!.add(UtilityBillItem("전기세", 0, 0))
        mUtilityBillItems!!.add(UtilityBillItem("수도세", 0, 0))
        mUtilityBillItems!!.add(UtilityBillItem("가스비", 0, 0))

        mUtilityBillAdapter!!.setNotificationList(mUtilityBillItems)

        binding.dialBtnSet.setOnClickListener {
            for(i in 0 until mUtilityBillAdapter!!.getItemCount()) {
                val term = mUtilityBillAdapter!!.getItemTerm(i)
                val day = mUtilityBillAdapter!!.getItemDay(i)
                enroll(term, day)
            }
            dismiss()
        }
        binding.dialBtnExit.setOnClickListener {
            dismiss()   // 대화상자를 닫는 함수
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun enroll(term: Int?, day: Int?) {
        mActivity.addAlarm(term!!, day!!)
    }

    override fun onClikDelete(position: Int?) {
        mUtilityBillItems!!.removeAt(position!!)
        mUtilityBillAdapter!!.setNotificationList(mUtilityBillItems)
    }
}