package kr.co.ajjulcoding.team.project.holo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentGpsBinding
import java.io.IOException


class GpsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var _binding: FragmentGpsBinding
    private var gMap: GoogleMap? = null
    private var latitude:Double = 37.568291
    private var longitude:Double = 126.997780
    private var validCpl:Boolean = false
    private var lastestLocation:SpannableString? = null
    private val binding get() = _binding
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    private lateinit var town:String
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mLocationRequest =  LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (checkPermissionForLocation(requireActivity())){ // 위치 권한 확인
            updateLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGpsBinding.inflate(inflater, container, false)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
        }
        binding.btnMap.setOnClickListener {
            updateLocation()
            val marker = LatLng(latitude, longitude)
            gMap!!.moveCamera(CameraUpdateFactory.zoomTo(15f))
            gMap!!.addMarker(MarkerOptions().position(marker).title("내 위치"))
            gMap!!.moveCamera(CameraUpdateFactory.newLatLng(marker))
        }
        lastestLocation?.let {
            binding.textLocation.setText(it)
        }
        binding.btnCpl.setOnClickListener {
            if (validCpl != true) {
                Toast.makeText(requireContext(), "현재위치가 업데이트되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }else{
                Log.d("동네 이름", "$town")
                (requireActivity() as MainActivity).setLocationToHome(town)
                (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
            }
        }
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {// 권한이 없으므로 권한 요청 알림 보내기
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
                false
            }
        } else {
            true
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: MutableMap<String, Boolean> ->
        val deniedList: List<String> = result.filter {
            !it.value
        }.map { it.key }
        when {
        deniedList.isNotEmpty() -> {
            val map = deniedList.groupBy { permission ->
                if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
            }
            map["DENIED"]?.let {
                // 뒤로 가기로 거부했을 때
                // request denied , request again
                Log.d("위치 권한", "onRequestPermissionsResult() _ 권한 허용 거부")
                (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
                Toast.makeText(requireActivity(), "위치 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
            }
            map["EXPLAINED"]?.let {
                // 거부 버튼 눌렀을 때
                // request denied ,send to settings
                Log.d("위치 권한", "한() _ 권한 허용 거부")
                (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
                Toast.makeText(requireActivity(), "위치 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
            }
        }
            else -> { // All request are permitted
                Log.d("위치 권한", "onRequestPermissionsResult() _ 권한 허용")
                updateLocation()
                binding.mapView.getMapAsync(this)
            }
        }
    }

    private fun updateLocation(){
        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        latitude = mLastLocation.latitude // 갱신 된 위도
        longitude = mLastLocation.longitude // 갱신 된 경도
        Log.d("변경 위치", "동작")
        convertAddress()
        val marker = LatLng(latitude, longitude)
        gMap?.uiSettings?.isMapToolbarEnabled = false
        gMap?.addMarker(MarkerOptions().position(marker).title("내 위치"))
        gMap?.moveCamera(CameraUpdateFactory.newLatLng(marker))
        gMap?.moveCamera(CameraUpdateFactory.zoomTo(15f))
    }

    // Default 위치 설정
    override fun onMapReady(googleMap: GoogleMap?) {
        val marker = LatLng(37.568291,126.997780)
        Log.d("디폴트 위치", "동작")
        gMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        gMap!!.uiSettings.isMapToolbarEnabled = false
        gMap!!.addMarker(MarkerOptions().position(marker).title("여기"))
        gMap!!.moveCamera(CameraUpdateFactory.newLatLng(marker))
        gMap!!.moveCamera(CameraUpdateFactory.zoomTo(15f))
    }

    private fun convertAddress(){
        try {
            val g:Geocoder= Geocoder(context)
            val address = g.getFromLocation(latitude, longitude, 10)
            if (address.size == 0){
                Toast.makeText(requireActivity(), "해당되는 주소정보가 없습니다.",Toast.LENGTH_SHORT).show()
            }
            else{
                validCpl = true
                town = address.get(0).thoroughfare
                val showTextAll: String = "\"현재 위치는 "+ town +" 입니다.\""
                val startTown = showTextAll.indexOf(town)
                val endTown = startTown+town.length
                val spannableString = SpannableString(showTextAll)

                spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#1D4999")),
                    startTown,endTown, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(StyleSpan(Typeface.BOLD)
                    , startTown, endTown, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#1D4999")),
                    0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#1D4999")),
                    showTextAll.length-1,showTextAll.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                lastestLocation = spannableString
                binding.textLocation.setText(spannableString)
            }
            Log.d("주소 변환",address.get(0).thoroughfare)
        }catch (e: IOException){
            Log.d("위치 주소 변환", "오류")
        }
    }
    // 생명 주기 맞춰주기
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }
    override fun onDestroy() {
        binding.mapView.onDestroy()
        super.onDestroy()
    }
}