package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var jobEmail:Job
    private lateinit var _binding:ActivityRegisterBinding
    private val binding get() = _binding
    private val checkMap = hashMapOf<String, Boolean>("realName" to false, "email" to false,
        "password" to false, "nickName" to false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOverlapCheck.setOnClickListener {
            // TODO("닉네임 중복 확인")
        }

        binding.btnSignin.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                checkName()
                checkPassword()
                checkNickName()
                preCheckEmail()
                if (checkMap["password"] == true) {
                    jobEmail = CoroutineScope(Dispatchers.Main).launch {
                        checkEmail()
                    }
                    jobEmail.join() // 기다림
                }
                val checkNum = checkMap.filterValues { it == false }
                Log.d("값 개수", checkNum.size.toString())
                if (checkNum.size == 0) {
                    val intentMain = Intent(this@RegisterActivity, FinishSplashActivity::class.java)
                    intent.action = Intent.ACTION_MAIN
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intentMain.flags =  // 로그인 성공시 기존 스택 모두 비우고 메인화면 생성"
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP //액티비티 스택제거
                    //TODO("MySQL에서 캐시에 넣을 정보 불러와서 캐시에 삽입, 비동기라서 뺑뺑이 UI 넣기 고려")
                    startActivity(intentMain)
                }
            }
        }
    }

    private fun checkName(){
        val editName = binding.editName
        val textName = editName.text.toString()
        if (textName == ""){
            editName.error = "이름을 입력해주세요."
            checkMap["realName"] = false
        }else{
            editName.error = null
            checkMap["realName"] = true
        }
    }

    private fun preCheckEmail(){
        val editEmail = binding.editEmail
        val textEmail = editEmail.text.toString()
        val pattern = android.util.Patterns.EMAIL_ADDRESS

        if (textEmail == ""){
            editEmail.error = "이메일을 입력해주세요."
            checkMap["email"] = false
        }else{
            if (pattern.matcher(textEmail).matches()){  // 정규 이메일 맞음
                editEmail.error = null
                checkMap["email"] = true
            }else{
                editEmail.error = "올바른 이메일 형식을 입력해주세요."
                checkMap["email"] = false
            }
        }
    }

    private suspend fun checkEmail(){
        val editEmail = binding.editEmail
        val textEmail = editEmail.text.toString()
        val textPassword = binding.editPassword.text.toString()

        if (checkMap["password"] == true) {
            try {
                coroutineScope {
                SettingInApp.mAuth.createUserWithEmailAndPassword(
                    textEmail,
                    textPassword
                ) // TODO("완료 스플래시 화면에서 로그인 메서드")
                    .addOnCompleteListener {
                        editEmail.error = null
                        checkMap["email"] = true
                    }
                }.await()
            }catch(e: FirebaseAuthUserCollisionException){
                editEmail.error = "이미 존재하는 이메일입니다."
                checkMap["email"] = false
            }
        }
    }

    private fun checkPassword(){
        val editPassword = binding.editPassword
        val editPasswoorCheck = binding.editPasswordCheck
        val textPassword = editPassword.text.toString()
        val textPasswordCheck = editPasswoorCheck.text.toString()

        if (textPassword == ""){
            editPassword.error = "비밀번호를 입력해주세요."
            checkMap["password"] = false
        }
        else if (textPassword.length < 8){
            editPassword.error = "8자 이상, 영문/숫자/기호 사용 가능"
            checkMap["password"] = false
        }
        else if (textPassword != textPasswordCheck){
            editPasswoorCheck.error = "위의 비밀번호와 일치하지 않습니다."
            checkMap["password"] = false
        }
        else{
            editPassword.error = null
            editPasswoorCheck.error = null
            checkMap["password"] = true
        }
    }

    private fun checkNickName(){
        val editNickname = binding.editNickname
        val textNickname = editNickname.text.toString()

    // TODO("데베 연동 후 중복 체크")
        if (textNickname == ""){
            editNickname.error = "닉네임을 입력해주세요."
            checkMap["nickName"] = false
        }else{
            editNickname.error = null
            checkMap["nickName"] = true
        }
    }
}