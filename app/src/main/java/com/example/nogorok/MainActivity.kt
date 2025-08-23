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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
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

    companion object {
        // 재연동 완료 등 외부에서 특정 화면으로 보내고 싶을 때 사용
        const val EXTRA_NAV_DEST = "NAV_DEST"
        const val NAV_HOME = "home"
        const val NAV_SCHEDULE = "schedule"
        const val NAV_SHORTREST = "shortrest"
    }

    private var isFabOpen = false
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navController: NavController

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

        // ✅ 토큰 설정(백업용). App에서 setTokenProvider를 이미 연결했다면 없어도 무방
        val token = TokenManager.getAccessToken(this)
        Log.d("TOKEN_CHECK", "AccessToken: $token")
        RetrofitClient.setAccessToken(token)

        // Nav + BottomNav
        bottomNavigationView = findViewById(R.id.navigation_main)
        bottomNavigationView.itemIconTintList = null
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)

        // ✅ 앱 최초 실행 시 인텐트 처리 (딥링크 우선)
        dispatchIntent(intent)

        // FABs
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

        fabShort.setOnClickListener { showShortRest() }
        fabLong.setOnClickListener { startActivity(Intent(this, LongRestActivity::class.java)) }
        fabDiary.setOnClickListener { DiaryDialogFragment().show(supportFragmentManager, "DiaryDialog") }
    }

    // 앱이 살아있는 상태에서 재호출될 때
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent ?: return
        setIntent(intent)
        dispatchIntent(intent)
    }

    /** 인텐트 일괄 처리: 1) 딥링크 우선 2) 명시적 목적지 분기 */
    private fun dispatchIntent(intent: Intent?) {
        intent ?: return
        // 🔑 네비 딥링크면 NavController가 자동으로 처리
        if (navController.handleDeepLink(intent)) return

        // 목적지 힌트 처리
        handleResultIntent(intent)

        // 동일 인텐트로 재실행 시 중복 처리 방지
        intent.removeExtra(EXTRA_NAV_DEST)
        intent.removeExtra("navigateTo")
        intent.removeExtra("autoShortRest")
        intent.removeExtra("date")
    }

    /**
     * 외부/다른 액티비티에서 온 의도 처리:
     *  - EXTRA_NAV_DEST 또는 "navigateTo" : home / schedule / shortrest
     *  - schedule의 경우 autoShortRest, date 전달 가능
     */
    private fun handleResultIntent(intent: Intent?) {
        intent ?: return

        val dest = intent.getStringExtra(EXTRA_NAV_DEST)
            ?: intent.getStringExtra("navigateTo")
            ?: return

        when (dest) {
            NAV_HOME, "home" -> {
                bottomNavigationView.selectedItemId = R.id.homeFragment
                runCatching { navController.navigate(R.id.homeFragment) }
                    .onFailure { Log.w("MainActivity", "navigate home failed: ${it.localizedMessage}") }
            }

            NAV_SCHEDULE, "schedule" -> {
                val auto = intent.getBooleanExtra("autoShortRest", false)
                val date = intent.getStringExtra("date")

                bottomNavigationView.selectedItemId = R.id.scheduleFragment

                val args = Bundle().apply {
                    putBoolean("autoShortRest", auto)
                    if (!date.isNullOrBlank()) putString("date", date)
                }
                runCatching { navController.navigate(R.id.scheduleFragment, args) }
                    .onFailure { Log.w("MainActivity", "navigate schedule failed: ${it.localizedMessage}") }
            }

            NAV_SHORTREST, "shortrest" -> {
                showShortRest()
            }
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

                val result: List<ShortRestResponse> =
                    RetrofitClient.shortRestApi.getShortRest(date = today)
                Log.d("ShortRestAPI", "✅ 응답 성공: ${result.size}건 수신")
                result.forEach {
                    Log.d(
                        "ShortRestItem",
                        "title=${it.title}, time=${it.startTime} - ${it.endTime}, sourceType=${it.sourceType}"
                    )
                }

                dialog.dismiss()

                val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val scheduleFragment =
                    navHostFragment.childFragmentManager.fragments.find { it is ScheduleFragment } as? ScheduleFragment

                val scheduleViewModel = scheduleFragment?.viewModel
                val selectedDate = scheduleViewModel?.selectedDate?.value ?: LocalDate.now()

                scheduleViewModel?.fetchGoogleEvents(this@MainActivity, selectedDate)

                // ✅ 다이얼로그 닫힌 후, 나의 일정 탭으로 전환
                bottomNavigationView.selectedItemId = R.id.scheduleFragment

            } catch (e: Exception) {
                dialog.dismiss()
                Log.e("ShortRestAPI", "❌ 요청 실패: ${e.localizedMessage}", e)
                Toast.makeText(this@MainActivity, "서버 요청 실패: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
