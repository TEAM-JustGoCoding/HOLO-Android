package kr.co.ajjulcoding.team.project.holo.util

import android.content.Context
import android.widget.Toast

class ToastUtil {
    companion object{
        fun showToast(context: Context, msg:String){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }
}