package kr.co.ajjulcoding.team.project.holo

import android.content.Intent
import com.google.firebase.auth.FirebaseAuth

class SettingInApp {
    companion object{
        val mAuth:FirebaseAuth = FirebaseAuth.getInstance()
        fun uniqueActivity(intent:Intent){
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}

class User {
    companion object {
        const val USER_EMAIL = "userEmail"
        fun currentUser() = SettingInApp.mAuth.currentUser?.email
    }
}