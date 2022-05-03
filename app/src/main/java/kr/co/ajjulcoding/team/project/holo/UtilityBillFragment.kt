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
import androidx.fragment.app.DialogFragment
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentUtilityBillBinding


class UtilityBillFragment : DialogFragment() {
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private var _binding: FragmentUtilityBillBinding? = null
    private val binding get() = _binding!!

    private var mCalender: GregorianCalendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
        mCalender = GregorianCalendar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentUtilityBillBinding.inflate(inflater, container, false)
        val view = binding.root
        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val spin1 = binding.RoomSpinner
        val spin2 = binding.ElectSpinner
        val spin3 = binding.WaterSpinner
        val spin4 = binding.FireSpinner
        spin1.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.date_array, android.R.layout.simple_spinner_item)
        spin2.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.date_array, android.R.layout.simple_spinner_item)
        spin3.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.date_array, android.R.layout.simple_spinner_item)
        spin4.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.date_array, android.R.layout.simple_spinner_item)

        spin1.setEnabled(false)
        spin2.setEnabled(false)
        spin3.setEnabled(false)
        spin4.setEnabled(false)

        val cbox1 = binding.cboxAgreeRoom
        val cbox2 = binding.cboxAgreeElect
        val cbox3 = binding.cboxAgreeWater
        val cbox4 = binding.cboxAgreeFire

        cbox1.setOnClickListener {
            if (cbox1.isChecked == false){
                spin1.setEnabled(false)
            }
            else {
                spin1.setEnabled(true)
                binding.RoomSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        mActivity.addAlarm(position)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        mActivity.addAlarm(1)  //클릭이벤트 없으면 default로 매달 1일
                    }
                }
            }
        }
        cbox2.setOnClickListener {
            if (cbox2.isChecked == false){
                spin2.setEnabled(false)
            }
            else {
                spin2.setEnabled(true)
                binding.ElectSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        mActivity.addAlarm(position)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        mActivity.addAlarm(1)  //클릭이벤트 없으면 default로 매달 1일
                    }
                }
            }
        }
        cbox3.setOnClickListener {
            if (cbox3.isChecked == false){
                spin3.setEnabled(false)
            }
            else {
                spin3.setEnabled(true)
                binding.WaterSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        mActivity.addAlarm(position)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        mActivity.addAlarm(1)  //클릭이벤트 없으면 default로 매달 1일
                    }
                }
            }
        }
        cbox4.setOnClickListener {
            if (cbox4.isChecked == false){
                spin4.setEnabled(false)
            }
            else {
                spin4.setEnabled(true)
                binding.FireSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        mActivity.addAlarm(position)
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        mActivity.addAlarm(1)  //클릭이벤트 없으면 default로 매달 1일
                    }
                }
            }
        }

        binding.dialBtnSet.setOnClickListener {
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