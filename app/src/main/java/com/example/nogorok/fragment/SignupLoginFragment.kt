package com.example.nogorok.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nogorok.R

// 로그인/회원가입 프래그먼트 (SplashPagerAdapter에서 마지막 페이지로 사용)
class SignupLoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 임시로 간단한 뷰만 반환 (나중에 레이아웃 연결)
        return inflater.inflate(R.layout.fragment_signup_login, container, false)
    }
}
