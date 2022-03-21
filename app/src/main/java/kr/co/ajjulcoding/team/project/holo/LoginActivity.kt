package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLoginBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { checkLogin() }
        binding.btnSignin.setOnClickListener {
            val intentInfAg = Intent(this, InformAgreeActivity::class.java)
            SettingInApp.uniqueActivity(intentInfAg)
            startActivity(intentInfAg)
        }
    }

    private fun checkLogin() { // TODO("로그인 유효성 확인")
        val editEmail = binding.editEmail
        val editPassword = binding.editPassword
        val inputId:String = editEmail.text.toString() ?: ""
        val inputPassword:String = editPassword.text.toString() ?: ""

        if (inputId == ""){
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        else if (inputPassword == ""){
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        SettingInApp.mAuth.signInWithEmailAndPassword(inputId,inputPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    Log.d("로그인", "createUserWithEmail:success")
                    val user: FirebaseUser = SettingInApp.mAuth.getCurrentUser()!!
                    val intentMain = Intent(this, MainActivity::class.java)
                    intentMain.putExtra(User.USER_EMAIL, User.currentUser())    // 사용자 인식 정보: email
                    SettingInApp.uniqueActivity(intentMain)
                    intent.action = Intent.ACTION_MAIN
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intentMain.flags =  // 로그인 성공시 기존 스택 모두 비우고 메인화면 생성"
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP //액티비티 스택제거
                    //TODO("MySQL에서 캐시에 넣을 정보 불러와서 캐시에 삽입, 비동기라서 뺑뺑이 UI 넣기 고려")
                    startActivity(intentMain)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("로그인", "createUserWithEmail:failure", task.getException())
                    Toast.makeText(this, "잘못된 이메일 또는 비밀번호입니다.", Toast.LENGTH_SHORT).show()
                }
                Log.d("로그인","성공:${task.exception}")
            }
            .addOnFailureListener {
                Log.d("로그인","실패:$it")
            }
    }
}