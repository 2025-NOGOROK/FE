package com.example.nogorok.features.rest.longrest

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.nogorok.R
import java.text.SimpleDateFormat
import java.util.*

class LongRestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_rest)

        val dateFormat = SimpleDateFormat("M월 d일", Locale.KOREA)
        val today = dateFormat.format(Date())

        val textView = findViewById<TextView>(R.id.restDateDescription)
        textView.text = "$today 의 긴 쉼표를 추천해드릴게요.\n마음에 드는 시나리오를 골라주세요."

        findViewById<TextView>(R.id.titleText).text = "긴 쉼표 추천"
        findViewById<ImageView>(R.id.backButton).setOnClickListener { finish() }

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val scenarioLabel = findViewById<TextView>(R.id.scenarioLabel)
        val btnPrev = findViewById<TextView>(R.id.btnPrev)
        val btnNext = findViewById<TextView>(R.id.btnNext)

        viewPager.adapter = ScenarioPagerAdapter(this)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scenarioLabel.text = "시나리오 ${position + 1}"
            }
        })

        btnPrev.setOnClickListener {
            viewPager.currentItem = (viewPager.currentItem - 1).coerceAtLeast(0)
        }

        btnNext.setOnClickListener {
            viewPager.currentItem = (viewPager.currentItem + 1).coerceAtMost(2)
        }
    }
}
