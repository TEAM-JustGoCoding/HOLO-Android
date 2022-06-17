package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activity = requireActivity() as MainActivity
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            mActivity.changeFragment(AppTag.SETTING_TAG)
        }
        binding.buttonPhotoedit.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            mActivity.getImgCallback().launch(intent)
        }
        binding.btnFinish.setOnClickListener {
            selectedUri?.let {
                val fileName = "profile_" + currentUser.uid.replace(".", "") + ".jpg"
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
                mActivity.setProfileImg(fileName)
                mActivity.changeFragment(AppTag.SETTING_TAG)
            }
        }.addOnFailureListener{
            Log.d("프로필 이미지 변경 오류", it.toString())
        }
    }

    fun setProfileImg(uri: Uri){
        selectedUri = uri
        binding.profilephoto.setImageBitmap(null)   // glide로 설정한 이미지 제거
        binding.profilephoto.setImageURI(null)
        binding.profilephoto.setImageURI(selectedUri)
        Log.d("사진 가져오기", "$uri")
    }


}