package kr.co.ajjulcoding.team.project.holo.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kr.co.ajjulcoding.team.project.holo.AppTag
import kr.co.ajjulcoding.team.project.holo.repository.Repository
import kr.co.ajjulcoding.team.project.holo.SettingInApp
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    private lateinit var job:Job
    private lateinit var _binding: ActivityLoginBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        //TODO("아이디/비번 일일이 치기 싫어서 설정함, 나중에 삭제")
//        binding.editEmail.setText("leeyeah8245@gmail.com")
//        binding.editPassword.setText("lyy828282")
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager // 키보드 내리기
            imm.hideSoftInputFromWindow(binding.editEmail.windowToken,0)
            imm.hideSoftInputFromWindow(binding.editPassword.windowToken,0) // imm 중복 사용 가능
            checkLogin()
        }
        binding.btnSignin.setOnClickListener {
            val intentInfAg = Intent(this, InformAgreeActivity::class.java)
            SettingInApp.uniqueActivity(intentInfAg)
            startActivity(intentInfAg)
        }
    }

    private fun checkLogin() { // TODO("로그인 유효성 확인 & 알림 editText error로 바꾸기")
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
                    val repository = Repository()
                    val intentMain = Intent(this, MainActivity::class.java)
                    SettingInApp.uniqueActivity(intentMain)
                    intent.action = Intent.ACTION_MAIN
                    intent.addCategory(Intent.CATEGORY_LAUNCHER)
                    intentMain.flags =  // 로그인 성공시 기존 스택 모두 비우고 메인화면 생성"
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TOP //액티비티 스택제거
                    //TODO("DB에서 캐시에 넣을 정보 불러오기, 메인에서 캐시 삽입, 비동기라서 뺑뺑이 UI 넣기 고려")
                    CoroutineScope(Dispatchers.Main).launch {
                        val result = repository.getUserInfo(AppTag.currentUserEmail()!!)

                        Log.d("로그인 데이터 확인", result.toString())
                        if (null != result) {
                            intentMain.putExtra(AppTag.USER_INFO, result)
                            intentMain.putExtra(AppTag.LOGIN_TAG, true) // 캐시 저장 지시
                            startActivity(intentMain)
                        }else Toast.makeText(this@LoginActivity,"서버 통신 오류",Toast.LENGTH_SHORT).show()
                    }
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