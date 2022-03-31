package kr.co.ajjulcoding.team.project.holo

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentProfileBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URLEncoder


class ProfileFragment(var currentUser:HoloUser) : Fragment() {
    private lateinit var _binding:FragmentProfileBinding
    private val binding get() = _binding
    private var selectedUri:Uri? = null
    lateinit var ivProfile: ImageView
    lateinit var imagePath: String
    lateinit var tempFile: File
    private var twiceValid = false
    private var temp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionForStorage(requireActivity())
    }

    private fun checkPermissionForStorage(context: Context): Boolean{
        // Android 6.0 Marshmallow 이상에서는 위치 권한에 추가 런타임 권한이 필요
        Log.d("저장소 권한 없음", "들어옴")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
                && context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
                    Log.d("저장소 권한 있음", "있음")
                    true
            } else {// 권한이 없으므로 권한 요청 알림 보내기
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))
                Log.d("저장소 권한 없음", "없음")
                if (twiceValid == true){
                    (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)
                    Toast.makeText(requireActivity(), "저장소 접근 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
                }
                false
            }
        } else {
            true
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: MutableMap<String, Boolean> ->
        Log.d("저장소 권한 없음3", "없음")
        val deniedList: List<String> = result.filter {
            !it.value
        }.map { it.key }
        when {
            deniedList.isNotEmpty() -> {
                val map = deniedList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
                }
                Log.d("저장소 권한 없음2", "없음${map}")
                map["DENIED"]?.let {
                    // 뒤로 가기로 거부했을 때
                    // request denied , request again
                    Log.d("저장소 권한", "onRequestPermissionsResult() _ 권한 허용 거부")
                    (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)
                    Toast.makeText(requireActivity(), "저장소 접근 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
                }
                map["EXPLAINED"]?.let {
                    // 거부 버튼 눌렀을 때
                    // request denied ,send to settings
                    Log.d("저장소 권한", "한() _ 권한 허용 거부")
                    twiceValid = true
                    (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)
                    Toast.makeText(requireActivity(), "저장소 접근 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
                }
            }
            else -> { // All request are permitted
                Log.d("저장소 권한", "onRequestPermissionsResult() _ 권한 허용")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView(){
        binding.textEmail.setText(currentUser.uid)
        binding.textNickname.setText(currentUser.nickName)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)
        }
        binding.btnFinish.setOnClickListener {
            if (selectedUri != null) {
//                (requireActivity() as MainActivity).postProfileImg(imagePath)
//                (requireActivity() as MainActivity).saveProfileCache(selectedUri.toString())
            }
            (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)

//            (requireActivity() as MainActivity).saveProfileCache(uri)
        }
    }


//
//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { result: MutableMap<String, Boolean> ->
//        val deniedList: List<String> = result.filter {
//            !it.value
//        }.map { it.key }
//        when {
//            deniedList.isNotEmpty() -> {
//                val map = deniedList.groupBy { permission ->
//                    if (shouldShowRequestPermissionRationale(permission)) "DENIED" else "EXPLAINED"
//                }
//                map["DENIED"]?.let {
//                    // 뒤로 가기로 거부했을 때
//                    // request denied , request again
//                    Log.d("위치 권한", "onRequestPermissionsResult() _ 권한 허용 거부")
//                    (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)
//                    Toast.makeText(requireActivity(), "위치 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
//                }
//                map["EXPLAINED"]?.let {
//                    // 거부 버튼 눌렀을 때
//                    // request denied ,send to settings
//                    Log.d("위치 권한", "한() _ 권한 허용 거부")
//                    (requireActivity() as MainActivity).changeFragment(AppTag.HOME_TAG)
//                    Toast.makeText(requireActivity(), "위치 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
//                }
//            }
//            else -> { // All request are permitted
//                Log.d("위치 권한", "onRequestPermissionsResult() _ 권한 허용")
//                updateLocation()
//                binding.mapView.getMapAsync(this)
//            }
//        }
//    }

    private fun initImageViewProfile() {
        ivProfile = binding.profilephoto

        ivProfile.setOnClickListener {
            when {
                // 갤러리 접근 권한이 있는 경우
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                -> {
                    navigateGallery()
                }

                // 갤러리 접근 권한이 없는 경우 & 교육용 팝업을 보여줘야 하는 경우
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                // 권한 요청 하기(requestPermissions) -> 갤러리 접근(onRequestPermissionResult)
                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    navigateGallery()
                else
                    Toast.makeText((requireActivity() as MainActivity), "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                //
            }
        }
    }

    private fun navigateGallery() {
        // Intent.ACTION_GET_CONTENT: 핸드폰의 컨텐츠를 가져오는 안드로이드 내장 액티비티를 시작한다.
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // 가져올 컨텐츠들 중에서 Image 만을 가져온다.
        intent.type = "image/*"
        // 갤러리에서 이미지를 선택한 후, 프로필 이미지뷰를 수정하기 위해 갤러리에서 수행한 값을 받아오는 startActivityForeResult를 사용한다.
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 예외처리
        if (resultCode != Activity.RESULT_OK)
            return

        when (requestCode) {
            // 2000: 이미지 컨텐츠를 가져오는 액티비티를 수행한 후 실행되는 Activity 일 때만 수행하기 위해서
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    //(requireActivity() as MainActivity).setPermission(selectedImageUri)
//                    val converter = BitmapConverter()
                    //val bmp = (requireActivity() as MainActivity).uriToBitmap(selectedImageUri) as Bitmap
                    imagePath = getRealPathFromUri(selectedImageUri)
                    var cursor: Cursor? = null
                    try {
                        val proj = arrayOf(MediaStore.Images.Media.DATA)
                        assert(selectedImageUri != null)
                        cursor = requireContext().contentResolver.query(selectedImageUri, proj, null, null, null)
                        assert(cursor != null)
                        val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        cursor.moveToFirst()
                        tempFile = File(cursor.getString(column_index))
                    } finally {
                        if (cursor != null) {
                            cursor.close()
                        }
                    }
                    setImage()
                    //ivProfile.setImageURI(selectedImageUri)
                    selectedUri = selectedImageUri
                    //(requireActivity() as MainActivity).changeProfile(bmp)

                    //Glide.with(this).load(ivProfile).circleCrop().into(binding.profilephoto)
                } else {
                    Toast.makeText((requireActivity() as MainActivity), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText((requireActivity() as MainActivity), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder((requireActivity() as MainActivity))
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }



    private fun getRealPathFromUri(uri: Uri): String {
        var column_index = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireContext().contentResolver.query(uri, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            }
        }
        return cursor!!.getString(column_index)
    }

    private fun setImage() {
        val options = BitmapFactory.Options()
        val originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options)
        BitMapToString(originalBm)
        ivProfile.setImageBitmap(originalBm)
    }

    private fun BitMapToString(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos) //bitmap compress

        val arr: ByteArray = baos.toByteArray()
        val image: String = Base64.encodeToString(arr, Base64.DEFAULT)
        try {
            temp = "&imagedevice=" + URLEncoder.encode(image, "utf-8")
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        } catch (e: OutOfMemoryError) {
            Toast.makeText(context, "이미지 용량이 너무 큽니다.", Toast.LENGTH_SHORT).show()
        }
    }

}