// WeeklyReportActivity.kt
package com.example.nogorok.features.report

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.nogorok.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

class WeeklyReportActivity : AppCompatActivity() {

    private lateinit var tvWeekTitle: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var btnBack: ImageView
    private lateinit var barChartContainer: LinearLayout
    private lateinit var emotionGraphContainer: FrameLayout
    private lateinit var emotionLineView: EmotionLineView

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

        setupDummyGraph()
        setupEmotionGraph()
        setupWeeklyWeather(listOf("SUNNY", "CLOUDY", "SUNNY", "SUNNY", "SUNNY", "RAIN", "SUNNY"))

    }

    private fun getWeekInfo(): Pair<String, String> {
        val today = LocalDate.now()
        val startOfWeek = today.with(java.time.DayOfWeek.MONDAY)
        val endOfWeek = startOfWeek.plusDays(6)

        val formatter = DateTimeFormatter.ofPattern("M월 d일")
        val rangeStr = "${startOfWeek.format(formatter)}~${endOfWeek.format(formatter)}"

        val weekFields = WeekFields.of(Locale.KOREA)
        val weekOfMonth = today.get(weekFields.weekOfMonth())
        val monthStr = "${today.year}년 ${today.monthValue}월"
        val weekStr = "$monthStr ${weekOfMonth}째주"

        return Pair(weekStr, rangeStr)
    }

    private fun setupDummyGraph() {
        val stressValues = listOf(70, 50, 70, 100, 50, 90, 80)
        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")
        val barColors = listOf(
            R.color.graph_green, R.color.graph_green, R.color.graph_green,
            R.color.graph_brown, R.color.graph_green, R.color.graph_brown,
            R.color.graph_green
        )

        val maxValue = 100f
        val barMaxHeight = 160.dp

        for (i in stressValues.indices) {
            val dayLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            }

            val barHeight = (barMaxHeight * (stressValues[i] / maxValue)).toInt()

            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(10.dp, barHeight).apply {
                    bottomMargin = 8.dp
                }
                background = GradientDrawable().apply {
                    cornerRadius = 10.dp.toFloat()
                    setColor(ContextCompat.getColor(this@WeeklyReportActivity, barColors[i]))

                }
            }

            val label = TextView(this).apply {
                text = dayLabels[i]
                setTextColor(ContextCompat.getColor(this@WeeklyReportActivity, R.color.black))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                typeface = ResourcesCompat.getFont(this@WeeklyReportActivity, R.font.pretendard_medium)
                setPadding(18.dp,0 , 0, 0)
            }

            dayLayout.addView(bar)
            dayLayout.addView(label)
            barChartContainer.addView(dayLayout)
        }
    }

    private fun setupEmotionGraph() {
        val emotionValues = listOf(20, 40, 35, 90, 50, 30, 25)
        val emojiResIds = emotionValues.map {
            when {
                it >= 70 -> R.drawable.smile
                it >= 30 -> R.drawable.regular
                else -> R.drawable.angry
            }
        }

        val maxValue = 100f
        val iconSize = 40.dp
        val chartHeight = 180.dp

        emotionGraphContainer.removeViews(1, emotionGraphContainer.childCount - 1)


        val points = mutableListOf<Pair<Float, Float>>()

        emotionLineView.points = points
        emotionLineView.invalidate()

        if (emotionLineView.parent == null) {
            emotionGraphContainer.addView(emotionLineView)
        }

        emotionGraphContainer.post {
            val width = emotionGraphContainer.width
            val height = emotionGraphContainer.height

            for (i in emotionValues.indices) {
                val pointX = (width / 7f) * i + (width / 14f)
                val pointYRatio = emotionValues[i] / maxValue
                val pointY = chartHeight * pointYRatio

                points.add(Pair(pointX, pointY))

                // 이모지 위치 (꺾이는 점 기준)
                val imageView = ImageView(this).apply {
                    setImageResource(emojiResIds[i])
                    layoutParams = FrameLayout.LayoutParams(iconSize, iconSize)
                    translationX = pointX - iconSize / 2f
                    translationY = pointY - iconSize / 2f
                }

                // 요일 라벨은 아래 고정
                val label = TextView(this).apply {
                    text = listOf("월", "화", "수", "목", "금", "토", "일")[i]
                    setTextColor(ContextCompat.getColor(this@WeeklyReportActivity, R.color.black))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(this@WeeklyReportActivity, R.font.pretendard_medium)
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                        translationX = pointX - 20f
                        translationY = chartHeight + iconSize / 2f + 8.dp
                    }
                }

                emotionGraphContainer.addView(imageView)
                emotionGraphContainer.addView(label)


            }



        }
    }

    val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()


    private fun setupWeeklyWeather(weatherCodes: List<String>) {
        val emojiMap = mapOf(
            "SUNNY" to "✨",
            "CLOUDY" to "☁️",
            "RAIN" to "🌧️",
            "SNOW" to "❄️"
        )

        val dayLabels = listOf("월", "화", "수", "목", "금", "토", "일")

        val container = findViewById<LinearLayout>(R.id.weatherContainer)
        container.removeAllViews()

        for (i in 0 until 7) {
            val dayWeather = weatherCodes.getOrNull(i) ?: "SUNNY"
            val emoji = emojiMap[dayWeather] ?: "💬"

            val outer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val background = TextView(this).apply {
                text = emoji
                textSize = 20f
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_weather_circle)
                layoutParams = LinearLayout.LayoutParams(37.dp, 45.dp).apply {
                    bottomMargin = 6.dp
                }
            }

            val day = TextView(this).apply {
                text = dayLabels[i]
                setTextColor(ContextCompat.getColor(this@WeeklyReportActivity, R.color.ivory))
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                typeface =
                    ResourcesCompat.getFont(this@WeeklyReportActivity, R.font.pretendard_medium)
                setBackgroundResource(R.drawable.bg_weather_day)
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(20.dp, 20.dp).apply { topMargin = (-12).dp }
            }

            outer.addView(background)
            outer.addView(day)
            container.addView(outer)
        }


    }
}
