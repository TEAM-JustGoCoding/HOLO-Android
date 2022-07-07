package kr.co.ajjulcoding.team.project.holo.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kr.co.ajjulcoding.team.project.holo.SettingInApp
import kr.co.ajjulcoding.team.project.holo.base.BaseActivity
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityCertificationBinding
import kr.co.ajjulcoding.team.project.holo.util.ToastUtil
import java.util.*
import java.util.concurrent.TimeUnit

class CertificationActivity : BaseActivity<ActivityCertificationBinding>({
    ActivityCertificationBinding.inflate(it)
}) {
    private var validTimeOut = MutableLiveData<Boolean>()   // 연속으로 인증번호 전송 X
    private var timeTask:Timer? = null
    private var storedVerificationId = ""
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val callbacks by lazy {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // 번호인증 혹은 기타 다른 인증(구글로그인, 이메일로그인 등) 끝난 상태
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("이미 다른/과거에 인증 완료, 그래도 이후 인증 정상 실행됨", "onVerificationCompleted:$credential")
                return
            }

            // 번호인증 실패 상태
            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("인증 실패", "onVerificationFailed", e)
                if (e is FirebaseAuthInvalidCredentialsException) {
                    ToastUtil.showToast(this@CertificationActivity, "인증에 실패했습니다.\n다시 요청해주세요.")// Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }
            }

            // 전화번호는 확인 되었으나 인증코드를 입력해야 하는 상태
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("인증 번호 입력 필요", "onCodeSent:$verificationId")
                ToastUtil.showToast(this@CertificationActivity,"인증 메시지 전송 완료!")
                countTime()
                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId // verificationId 와 전화번호인증코드 매칭해서 인증하는데 사용예정
                resendToken = token
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnSendSMS.setOnClickListener {
            if (binding.editPhoneNum.text.toString() == "")
                ToastUtil.showToast(this, "핸드폰 번호를 입력해주세요.")
            else {
                val editPhoneNum = binding.editPhoneNum
                val inputPhoneNum:String = "+82"+editPhoneNum.text.toString().substring(1)
                Log.d("인증 폰번호 확인", inputPhoneNum)
                ToastUtil.showToast(this, "인증 메시지 전송 중..")
                sendSMS(inputPhoneNum)
            }
        }
        binding.btnConfirm.setOnClickListener {
            try {
                val phoneCredential =
                    PhoneAuthProvider.getCredential(
                        storedVerificationId, binding.editCertiNum.text.toString())
                signInWithPhoneAuthCredential(phoneCredential)
            } catch (e: Exception) {
                Log.d("인증 코드 불일치", e.toString())
                ToastUtil.showToast(this,"인증 코드 불일치")
            }
        }
    }

    private fun countTime(){
        val textTime = binding.textTime
        var time:Int = 60 // 60초
        timeTask = kotlin.concurrent.timer(period = 1000){
            runOnUiThread {
                if (time == 0){
                    stopTimer()
                    validTimeOut.value = true
                    textTime.setText("입력 시간이 만료됐습니다.\n인증번호를 다시 전송해주세요.")
                    return@runOnUiThread
                }
                textTime.setText("입력 시간: $time 초")
                time--
            }
        }
    }

    private fun stopTimer(){
        timeTask?.cancel()
        binding.btnSendSMS.isEnabled = true
    }

    private fun sendSMS(phoneNum:String){
        val options = PhoneAuthOptions.newBuilder(SettingInApp.mAuth)
            .setPhoneNumber(phoneNum)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // 1분 시간 제한
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)   // SMS 전송
        binding.btnSendSMS.isEnabled = false
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential){
        Log.d("인증 유무", "진행중")
        SettingInApp.mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("인증 성공", "성공!")
                    val intentRegist = Intent(this, RegisterActivity::class.java)
                    SettingInApp.uniqueActivity(intentRegist)
                    startActivity(intentRegist) // TODO("RegisterActivity내부에서 뒤로가기시 전번 정보 삭제 구현")
                } else {
                    Log.d("인증 실패", "${task.exception}")
                    ToastUtil.showToast(this,"인증 번호 불일치")
                }
            }
    }
}