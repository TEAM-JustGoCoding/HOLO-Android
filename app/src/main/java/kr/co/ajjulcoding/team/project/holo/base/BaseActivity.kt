package kr.co.ajjulcoding.team.project.holo.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.CallSuper
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B: ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B   //  lambda: LayoutInflater를 인자로 받고 ViewBinding관련 타입을 반환
) : AppCompatActivity() {

    private var _binding: B? = null
    val binding get() = _binding!!

    @CallSuper // 반드시 호출해야한다고 제약 조건 걸어주는 것
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}