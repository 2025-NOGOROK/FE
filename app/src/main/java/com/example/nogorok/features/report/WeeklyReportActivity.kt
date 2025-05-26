package com.example.nogorok.features.report

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.nogorok.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*
import android.view.View
import android.widget.LinearLayout


class WeeklyReportActivity : AppCompatActivity() {

    private lateinit var tvWeekTitle: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var btnBack: ImageView
    private lateinit var barChartContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weekly_report)

        tvWeekTitle = findViewById(R.id.tvWeekTitle)
        tvDateRange = findViewById(R.id.tvDateRange)
        btnBack = findViewById(R.id.btnBack)
        barChartContainer = findViewById(R.id.barContainer)

        val (weekTitle, dateRange) = getWeekInfo()
        tvWeekTitle.text = weekTitle
        tvDateRange.text = dateRange

        btnBack.setOnClickListener {
            finish() // 뒤로가기
        }

        setupDummyGraph()
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
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
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

    // dp 변환 확장 함수
    val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
