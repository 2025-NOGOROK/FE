package com.example.nogorok

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nogorok.features.rest.diary.DiaryActivity
import com.example.nogorok.features.rest.longrest.LongRestActivity
import com.example.nogorok.features.rest.shortrest.ShortRestActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private var isFabOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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

        // 열림/닫힘 애니메이션
        val animOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open)
        val animClose = AnimationUtils.loadAnimation(this, R.anim.fab_close)
        val rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open)
        val rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close)

        // 클릭 이벤트
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

        // 이동 이벤트
        fabShort.setOnClickListener {
            startActivity(Intent(this, ShortRestActivity::class.java))
        }

        fabLong.setOnClickListener {
            startActivity(Intent(this, LongRestActivity::class.java))
        }

        fabDiary.setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))
        }
    }
}
