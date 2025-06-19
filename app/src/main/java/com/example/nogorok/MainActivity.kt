package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.nogorok.databinding.ActivityMainBinding
import com.example.nogorok.features.rest.diary.DiaryDialogFragment
import com.example.nogorok.features.rest.longrest.LongRestActivity
import com.example.nogorok.features.rest.shortrest.ShortRestFragment
import com.example.nogorok.features.schedule.ScheduleFragment
import com.example.nogorok.network.RetrofitClient
import com.example.nogorok.network.dto.ShortRestResponse
import com.example.nogorok.utils.TokenManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        // ✅ 토큰 설정 및 로그
        val token = TokenManager.getAccessToken(this)
        Log.d("TOKEN_CHECK", "AccessToken: $token")
        RetrofitClient.setAccessToken(token)

        // Bottom Navigation 연결
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigation_main)
        bottomNavigationView.itemIconTintList = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        // 플로팅 버튼들
        val fabMain = findViewById<ImageButton>(R.id.fabMain)
        val fabMenu = findViewById<LinearLayout>(R.id.fabMenu)
        val fabShort = findViewById<LinearLayout>(R.id.fabShort)
        val fabLong = findViewById<LinearLayout>(R.id.fabLong)
        val fabDiary = findViewById<LinearLayout>(R.id.fabDiary)

        // 애니메이션
        val animOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val animClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        val rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open)
        val rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close)

        // 메인 FAB 클릭
        fabMain.setOnClickListener {
            isFabOpen = !isFabOpen
            if (isFabOpen) {
                fabMain.startAnimation(rotateOpen)
                fabMenu.visibility = View.VISIBLE
                fabMenu.startAnimation(animOpen)
            } else {
                fabMain.startAnimation(rotateClose)
                fabMenu.startAnimation(animClose)
                fabMenu.postDelayed({ fabMenu.visibility = View.GONE }, 200)
            }
        }

        // 짧은 쉼표 추천
        fabShort.setOnClickListener {
            showShortRest()
        }

        // 긴 쉼표 이동
        fabLong.setOnClickListener {
            startActivity(Intent(this, LongRestActivity::class.java))
        }

        // 하루일기 다이얼로그
        fabDiary.setOnClickListener {
            DiaryDialogFragment().show(supportFragmentManager, "DiaryDialog")
        }
    }

    private fun showShortRest() {
        val dialog = ShortRestFragment()
        dialog.show(supportFragmentManager, "ShortRest")

        val today = LocalDate.now().toString()
        Log.d("ShortRestAPI", "🔄 요청 시작: $today")

        lifecycleScope.launch {
            try {
                val token = TokenManager.getAccessToken(this@MainActivity)
                if (token == null) {
                    Toast.makeText(this@MainActivity, "JWT 토큰이 없습니다. 로그인 필요", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    return@launch
                }

                val result: List<ShortRestResponse> = RetrofitClient.shortRestApi.getShortRest(date = today)
                Log.d("ShortRestAPI", "✅ 응답 성공: ${result.size}건 수신")
                result.forEach {
                    Log.d("ShortRestItem", "title=${it.title}, time=${it.startTime} - ${it.endTime}, sourceType=${it.sourceType}")
                }

                dialog.dismiss()

                // 📌 ScheduleFragment의 ViewModel에 fetchGoogleEvents 호출
                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val scheduleFragment =
                    navHostFragment.childFragmentManager.fragments.find { it is ScheduleFragment } as? ScheduleFragment

                val scheduleViewModel = scheduleFragment?.viewModel
                val selectedDate = scheduleViewModel?.selectedDate?.value ?: LocalDate.now()

                scheduleViewModel?.fetchGoogleEvents(this@MainActivity, selectedDate)

            } catch (e: Exception) {
                dialog.dismiss()
                Log.e("ShortRestAPI", "❌ 요청 실패: ${e.localizedMessage}", e)
                Toast.makeText(this@MainActivity, "서버 요청 실패: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
