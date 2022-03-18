package kr.co.ajjulcoding.team.project.holo

import android.content.Intent

class SettingInActivity {
    companion object{
        fun uniqueActivity(intent:Intent){
            intent.action = Intent.ACTION_MAIN
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }
}