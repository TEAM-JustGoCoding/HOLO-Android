package kr.co.ajjulcoding.team.project.holo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.databinding.FragmentProfileBinding


class ProfileFragment(var currentUser:HoloUser) : Fragment() {
    private lateinit var _binding:FragmentProfileBinding
    private val binding get() = _binding
    private lateinit var _activity:MainActivity
    private val mActivity get() = _activity
    private var selectedUri:Uri? = null
    private var twiceValid = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionForStorage(requireActivity())
        _activity = requireActivity() as MainActivity
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
                    (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
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
                    (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
                    Toast.makeText(requireActivity(), "저장소 접근 권한이 없어 해당 기능을 수행할 수 없습니다!", Toast.LENGTH_SHORT).show()
                }
                map["EXPLAINED"]?.let {
                    // 거부 버튼 눌렀을 때
                    // request denied ,send to settings
                    Log.d("저장소 권한", "한() _ 권한 허용 거부")
                    twiceValid = true
                    (requireActivity() as MainActivity).changeFragment(AppTag.SETTING_TAG)
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
        currentUser.profileImg?.let {
            Glide.with(_activity).load(Uri.parse(it)).apply {
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }.into(binding.profilephoto)
        }
    }

    private var imgLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { // 사진 정상적으로 가져옴
            selectedUri = uri
            binding.profilephoto.setImageBitmap(null)   // glide로 설정한 이미지 제거
            binding.profilephoto.setImageURI(null)
            binding.profilephoto.setImageURI(selectedUri)
            Log.d("사진 가져오기", "$uri")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            mActivity.changeFragment(AppTag.SETTING_TAG)
        }
        binding.profilephoto.setOnClickListener {
            imgLauncher.launch("image/*")
        }
        binding.btnFinish.setOnClickListener {
            selectedUri?.let {
                val fileName = "profile_" + currentUser.uid!!.replace(".", "") + ".jpg"
                createProfile(fileName) // 기존에 같은 이름이 존재하면 덮어쓰는듯
            }

        }
    }

    private fun createProfile(fileName:String){
        Toast.makeText(mActivity, "프로필 이미지 번경 중..", Toast.LENGTH_SHORT).show()
        val FBstorage = FirebaseStorage.getInstance()
        val FBstorageRef = FBstorage.reference
        val postRef = FBstorageRef.child("profile_img/"+fileName)
        val uploadTask:UploadTask = postRef.putFile(selectedUri!!)
        uploadTask.addOnSuccessListener {
            CoroutineScope(Dispatchers.Main).launch {
                mActivity.setProfileImgToHome(fileName)
                mActivity.changeFragment(AppTag.SETTING_TAG)
            }
        }.addOnFailureListener{
            Log.d("프로필 이미지 변경 오류", it.toString())
        }
    }
}