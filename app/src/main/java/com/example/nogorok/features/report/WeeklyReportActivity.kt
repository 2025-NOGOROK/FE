package com.example.nogorok.features.report

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.example.nogorok.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import android.util.Log

class WeeklyReportActivity : AppCompatActivity() {

    private val viewModel: WeeklyReportViewModel by viewModels()

    private lateinit var tvWeekTitle: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var btnBack: ImageView
    private lateinit var barChartContainer: LinearLayout
    private lateinit var emotionGraphContainer: FrameLayout
    private lateinit var emotionLineView: EmotionLineView

    private var cachedEmotionValues: List<Int>? = null
    private var cachedEmotionNames: List<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_report)

        tvWeekTitle = findViewById(R.id.tvWeekTitle)
        tvDateRange = findViewById(R.id.tvDateRange)
        btnBack = findViewById(R.id.btnBack)
        barChartContainer = findViewById(R.id.barContainer)
        emotionGraphContainer = findViewById(R.id.emotionGraphContainer)
        emotionLineView = findViewById(R.id.emotionLineView)

        val (weekTitle, dateRange) = getWeekInfo()
        tvWeekTitle.text = weekTitle
        tvDateRange.text = dateRange

        btnBack.setOnClickListener { finish() }

        viewModel.emotionValues.observe(this) { values ->
            Log.d("EmotionGraph", "emotionValues: $values")
            cachedEmotionValues = values
            if (cachedEmotionNames != null) {
                setupEmotionGraph(cachedEmotionValues!!, cachedEmotionNames!!)
            }
        }

        viewModel.emotionNames.observe(this) { names ->
            Log.d("EmotionGraph", "emotionNames: $names")
            cachedEmotionNames = names
            if (cachedEmotionValues != null) {
                setupEmotionGraph(cachedEmotionValues!!, cachedEmotionNames!!)
            }
        }


        viewModel.stressValues.observe(this, Observer { setupDummyGraph(it) })
        viewModel.weatherValues.observe(this, Observer { setupWeeklyWeather(it) })

        viewModel.fetchWeeklyEmotion()
        viewModel.fetchWeeklyStress()
        viewModel.fetchWeeklyWeather()
    }

    private fun getWeekInfo(): Pair<String, String> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = startOfWeek.plusDays(6)
        val formatter = DateTimeFormatter.ofPattern("M월 d일")
        val rangeStr = "${startOfWeek.format(formatter)}~${endOfWeek.format(formatter)}"
        val weekFields = WeekFields.of(Locale.KOREA)
        val weekOfMonth = today.get(weekFields.weekOfMonth())
        val weekStr = "${today.year}년 ${today.monthValue}월 ${weekOfMonth}째주"
        return Pair(weekStr, rangeStr)
    }

    private fun setupDummyGraph(stressValues: List<Int>) {
        barChartContainer.removeAllViews()
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")
        val maxValue = 100f
        val barMaxHeight = 160.dp

        for (i in stressValues.indices) {
            val dayLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }

            val stressValue = stressValues[i]
            val barHeight = (barMaxHeight * (stressValue / maxValue)).toInt()
            val barColorResId = if (stressValue >= 50) R.color.graph_green else R.color.graph_brown

            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(10.dp, barHeight).apply { bottomMargin = 8.dp }
                background = GradientDrawable().apply {
                    cornerRadius = 10.dp.toFloat()
                    setColor(ContextCompat.getColor(this@WeeklyReportActivity, barColorResId))
                }
            }

            val label = TextView(this).apply {
                text = dayLabels[i]
                setTextColor(ContextCompat.getColor(this@WeeklyReportActivity, R.color.black))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                typeface = ResourcesCompat.getFont(this@WeeklyReportActivity, R.font.pretendard_medium)
                setPadding(18.dp, 0, 0, 0)
            }

            dayLayout.addView(bar)
            dayLayout.addView(label)
            barChartContainer.addView(dayLayout)
        }
    }

    private fun setupEmotionGraph(emotionValues: List<Int>?, emotionNames: List<String>?) {
        val safeValues = List(7) { i -> emotionValues?.getOrNull(i) ?: 0 }
        val safeNames = List(7) { i -> emotionNames?.getOrNull(i) ?: "null" }
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

        val emojiResIds = safeNames.map {
            when (it) {
                "기쁨" -> R.drawable.smile
                "보통" -> R.drawable.regular
                "우울" -> R.drawable.sad
                "짜증" -> R.drawable.irritated
                "화남" -> R.drawable.angry
                else -> R.drawable.regular
            }
        }

        val maxValue = 100f
        val iconSize = 40.dp
        val chartHeight = 180.dp

        emotionGraphContainer.removeViews(1, emotionGraphContainer.childCount - 1)
        val points = mutableListOf<Pair<Float, Float>>()


        if (emotionLineView.parent == null) {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                180.dp  // 반드시 고정 높이 설정
            )
            emotionLineView.layoutParams = params
            emotionGraphContainer.addView(emotionLineView)
        }

        emotionGraphContainer.post {
            val width = emotionGraphContainer.width

            for (i in safeValues.indices) {
                val pointX = (width / 7f) * i + (width / 14f)
                val pointY = chartHeight * (1f - safeValues[i] / maxValue)


                points.add(Pair(pointX, pointY))

                val imageView = ImageView(this).apply {
                    setImageResource(emojiResIds[i])
                    layoutParams = FrameLayout.LayoutParams(iconSize, iconSize)
                    translationX = pointX - iconSize / 2f
                    translationY = pointY - iconSize / 2f
                }

                val label = TextView(this).apply {
                    text = dayLabels[i]
                    setTextColor(ContextCompat.getColor(this@WeeklyReportActivity, R.color.black))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(this@WeeklyReportActivity, R.font.pretendard_medium)
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        translationX = pointX - 20f
                        translationY = chartHeight + iconSize / 2f + 8.dp
                    }
                }

                emotionGraphContainer.addView(imageView)
                emotionGraphContainer.addView(label)
            }
            emotionLineView.points = points
            emotionLineView.invalidate()
        }

    }


    private fun setupWeeklyWeather(weatherCodes: List<String>) {
        val emojiMap = mapOf("맑음" to "✨", "흐림" to "☁️", "비" to "🌧️", "눈" to "❄️")
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")
        val container = findViewById<LinearLayout>(R.id.weatherContainer)
        container.removeAllViews()

        for (i in 0 until 7) {
            val dayWeather = weatherCodes.getOrNull(i) ?: "💬"
            val emoji = emojiMap[dayWeather] ?: "💬"

            val outer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val background = TextView(this).apply {
                text = emoji
                textSize = 20f
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_weather_circle)
                layoutParams = LinearLayout.LayoutParams(37.dp, 45.dp).apply { bottomMargin = 6.dp }
            }

            val day = TextView(this).apply {
                text = dayLabels[i]
                setTextColor(ContextCompat.getColor(this@WeeklyReportActivity, R.color.ivory))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                typeface = ResourcesCompat.getFont(this@WeeklyReportActivity, R.font.pretendard_medium)
                setBackgroundResource(R.drawable.bg_weather_day)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(20.dp, 20.dp).apply { topMargin = (-12).dp }
            }

            outer.addView(background)
            outer.addView(day)
            container.addView(outer)
        }
    }

    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
