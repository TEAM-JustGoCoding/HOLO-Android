package kr.co.ajjulcoding.team.project.holo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.ajjulcoding.team.project.holo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object{
        const val HOME_TAG = "HomeFragment"
    }
    private lateinit var _binding:ActivityMainBinding
    private val binding get() = _binding
    private lateinit var homeFragment:HomeFragment
    private var currentTag:String = HOME_TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding.contraintMain.setOnClickListener { }
        if (intent.getBooleanExtra(AppTag.LOGIN_TAG, false)) {
            showHomeFragment(intent.getSerializableExtra(AppTag.USER_INFO) as HoloUser)
            saveCache()
        }else{  // TODO("캐쉬에서 데이터 뽑아오기")
//            val userInfo:HoloUser = getCache()
//            showHomeFragment(userInfo)
        }
    }

    override fun onBackPressed() {
        // TODO("뒤로 가기 버튼 2번 연속 눌러야 종료")
        super.onBackPressed()
    }

    private fun saveCache(){

    }

//    private fun getCache(): HoloUser{
//
//    }

    private fun showHomeFragment(userInfo:HoloUser){
        val tran = supportFragmentManager.beginTransaction()
        homeFragment = HomeFragment(userInfo)
        tran.add(R.id.fragmentView, homeFragment)
        tran.commit()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}