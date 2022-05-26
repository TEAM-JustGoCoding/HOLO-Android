package kr.co.ajjulcoding.team.project.holo

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class ChatListFragmentFactory(private val mUserInfo: HoloUser): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            ChatListFragment::class.java.name -> {
                Log.d("유저 정보 이니시에이트", mUserInfo.toString())
                ChatListFragment(mUserInfo)
            }
            else -> super.instantiate(classLoader, className)
        }
        //
    }
}