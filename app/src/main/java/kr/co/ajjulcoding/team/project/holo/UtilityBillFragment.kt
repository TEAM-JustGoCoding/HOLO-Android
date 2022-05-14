package kr.co.ajjulcoding.team.project.holo


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.GregorianCalendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentUtilityBillBinding


class UtilityBillFragment : DialogFragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private var _binding: FragmentUtilityBillBinding? = null
    private val binding get() = _binding!!
    private var mlistView: ListView? = null
    private var mUtilityBillAdapter: UtilityBillAdapter? = null
    private var mUtilityBillItems: ArrayList<UtilityBillItem>? = null
    private var mCalender: GregorianCalendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
        mCalender = GregorianCalendar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUtilityBillBinding.inflate(inflater, container, false)
        val view = binding.root

        mlistView = requireView().findViewById(R.id.listView)

        /* initiate adapter */
        mUtilityBillAdapter = UtilityBillAdapter(mUtilityBillItems!!)

        /* initiate recyclerview */
        mlistView!!.adapter = mUtilityBillAdapter

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var count = mUtilityBillAdapter!!.getItemCount()

        val appendbtn = binding.BtnAppend
        val modifybtn = binding.BtnModify
        val deletebtn = binding.BtnDelete

        appendbtn.setOnClickListener() {
            mUtilityBillItems!!.add(UtilityBillItem("월세"))
            mUtilityBillAdapter!!.setNotificationList(mUtilityBillItems)
        }

        deletebtn.setOnClickListener() {
            mUtilityBillItems!!.remove(UtilityBillItem("월세"))
            mUtilityBillAdapter!!.setNotificationList(mUtilityBillItems)
        }

        var day = 0

        val spin = mlistView.

        spin.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.term_array, android.R.layout.simple_spinner_item)

        spin.setEnabled(false)

        val cbox = binding.cboxAgree

        cbox.setOnClickListener {
            if (cbox.isChecked == false){
                spin.setEnabled(false)
            }
            else {
                spin.setEnabled(true)
                binding.DateSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        mActivity.addAlarm(position)
                        day = position
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
//                        mActivity.addAlarm(1)  //클릭이벤트 없으면 default로 매달 1일
                        day = 1
                    }
                }
            }
        }

        binding.dialBtnSet.setOnClickListener {
            if(day1 != 0) mActivity.addAlarm(day1)
            dismiss()
        }
        binding.dialBtnExit.setOnClickListener {
            dismiss()   // 대화상자를 닫는 함수
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}