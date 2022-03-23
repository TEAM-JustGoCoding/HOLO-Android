package kr.co.ajjulcoding.team.project.holo

import android.os.AsyncTask
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest


//class RegisterRequest(val postData:HashMap<String,String>, listener:Response.Listener<String>) :
//    StringRequest(Method.POST, PhpUrl.URL_REGISTER,listener, null){
//    // postData's key: uid(email):String, password:String, real_name:String, nick_name:String
//
//    @Throws(AuthFailureError::class)    //  AuthFailureError 예외 발생 가능 => TODO("사용할 때 try catch문 삽입")
//    protected override fun getParams(): MutableMap<String, String> {
//        return postData
//    }
//
//    //inner class InsertData : AsyncTask<Void,Void,String>
//}