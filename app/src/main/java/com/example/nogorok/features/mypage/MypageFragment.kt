package com.example.nogorok.features.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.nogorok.R
import com.example.nogorok.features.auth.login.SignupLoginActivity
import com.example.nogorok.utils.TokenManager

class MypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        // 사용자 이름 표시
        val tvUsername = view.findViewById<TextView>(R.id.tv_username)
        tvUsername.text = "김덕우님"

        // 로그아웃 버튼 처리 (프론트만 처리)
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // ✅ 토큰 삭제
            TokenManager.clearAccessToken(requireContext())

            Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()

            // 로그인 화면으로 이동
            val intent = Intent(requireContext(), SignupLoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }

        // 스트레스 설문 수정
        view.findViewById<LinearLayout>(R.id.layout_edit_survey).setOnClickListener {
            Toast.makeText(requireContext(), "설문 수정 화면으로 이동", Toast.LENGTH_SHORT).show()
        }

        // 회원 탈퇴
        view.findViewById<LinearLayout>(R.id.layout_withdraw).setOnClickListener {
            Toast.makeText(requireContext(), "회원 탈퇴 기능 실행", Toast.LENGTH_SHORT).show()
        }

        // 주간 리포트
        view.findViewById<LinearLayout>(R.id.layout_weekly_report).setOnClickListener {
            Toast.makeText(requireContext(), "주간 리포트 화면", Toast.LENGTH_SHORT).show()
        }

        // 월간 리포트
        view.findViewById<LinearLayout>(R.id.layout_monthly_report).setOnClickListener {
            Toast.makeText(requireContext(), "월간 리포트 화면", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
