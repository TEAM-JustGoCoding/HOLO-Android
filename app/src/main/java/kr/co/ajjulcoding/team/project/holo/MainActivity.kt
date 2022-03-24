package kr.co.ajjulcoding.team.project.holo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var _binding:ActivityMainBinding
    private val bindnig get() = _binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        bindnig.contraintMain.setOnClickListener { }
        if (intent.getBooleanExtra(AppTag.LOGIN_TAG, false)) {
            saveCache()
        }
    }

    private fun saveCache(){

    }
}