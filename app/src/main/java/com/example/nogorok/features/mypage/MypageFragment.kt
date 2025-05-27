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
import androidx.lifecycle.lifecycleScope
import com.example.nogorok.R
import com.example.nogorok.features.auth.login.SignupLoginActivity
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.utils.TokenManager
import kotlinx.coroutines.launch
import android.util.Log

class MypageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        // 사용자 이름 표시 (하드코딩된 예시)
        val tvUsername = view.findViewById<TextView>(R.id.tv_username)
        tvUsername.text = "김덕우님"

        // 로그아웃
        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            TokenManager.clearAccessToken(requireContext())
            Toast.makeText(requireContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            moveToLoginScreen()
        }

        // 스트레스 설문 수정
        view.findViewById<LinearLayout>(R.id.layout_edit_survey).setOnClickListener {
            Toast.makeText(requireContext(), "설문 수정 화면으로 이동", Toast.LENGTH_SHORT).show()
        }

        // ✅ 회원 탈퇴 기능
        view.findViewById<LinearLayout>(R.id.layout_withdraw).setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.mypageApi.deleteUser()
                    if (response.isSuccessful) {
                        val body = response.body()?.string() ?: ""
                        Log.d("탈퇴응답", body) // 서버가 "탈퇴 완료" 같은 텍스트를 줬을 때

                        TokenManager.clearAccessToken(requireContext())
                        Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show()
                        moveToLoginScreen()
                    } else {
                        Toast.makeText(requireContext(), "회원 탈퇴 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 주간 리포트 예시
        view.findViewById<LinearLayout>(R.id.layout_weekly_report).setOnClickListener {
            val intent = Intent(requireContext(), com.example.nogorok.features.report.WeeklyReportActivity::class.java)
            startActivity(intent)
        }


        // 월간 리포트 예시
        view.findViewById<LinearLayout>(R.id.layout_monthly_report).setOnClickListener {
            val intent = Intent(requireContext(), com.example.nogorok.features.report.MonthlyReportActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun moveToLoginScreen() {
        view?.post {
            val intent = Intent(requireContext(), SignupLoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
