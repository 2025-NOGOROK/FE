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
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.network.dto.DailyStressEntry
import java.time.LocalDate
import java.time.YearMonth
import android.widget.GridLayout

class MonthlyReportActivity : AppCompatActivity() {

    private lateinit var tvHeader: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var btnBack: ImageView
    private lateinit var emotionRatioContainer: LinearLayout
    private lateinit var emotionCalendarContainer: FrameLayout
    private val viewModel: MonthlyReportViewModel by lazy {
        ViewModelProvider(this)[MonthlyReportViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)

        tvHeader = findViewById(R.id.tvHeader)
        tvDateRange = findViewById(R.id.tvDateRange)
        btnBack = findViewById(R.id.btnBack)
        emotionRatioContainer = findViewById(R.id.emotionRatioContainer)
        emotionCalendarContainer = findViewById(R.id.emotionCalendarContainer)

        tvHeader.text = "월간 리포트"
        tvDateRange.text = getCurrentMonth()

        btnBack.setOnClickListener { finish() }

        viewModel.emotionData.observe(this) { data ->
            setupEmotionRatioGraph(data)
        }

        viewModel.dailyStressList.observe(this) { list ->
            setupMonthlyEmotionCalendar(list)
        }

        viewModel.fetchEmotionRatio()
        viewModel.fetchMonthlyStress()
    }

    private fun getCurrentMonth(): String {
        val today = LocalDate.now()
        return "${today.year}년 ${today.monthValue}월"
    }

    private fun setupEmotionRatioGraph(emotionMap: Map<String, Double>) {
        emotionRatioContainer.removeAllViews()

        val iconMap = mapOf(
            "JOY" to R.drawable.smile,
            "DEPRESSED" to R.drawable.sad,
            "NORMAL" to R.drawable.regular,
            "IRRITATED" to R.drawable.irritated,
            "ANGRY" to R.drawable.angry
        )

        val maxPercent = emotionMap.values.maxOrNull() ?: 1.0

        for ((code, percent) in emotionMap) {
            val iconRes = iconMap[code] ?: continue
            val label = when (code) {
                "JOY" -> "기쁨"
                "DEPRESSED" -> "우울"
                "NORMAL" -> "보통"
                "IRRITATED" -> "짜증"
                "ANGRY" -> "화남"
                else -> code
            }

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = 16.dp }
            }

            val emoji = ImageView(this).apply {
                setImageResource(iconRes)
                layoutParams = LinearLayout.LayoutParams(32.dp, 32.dp).apply {
                    rightMargin = 8.dp
                }
            }

            val barWidth = (200.dp * (percent / 100)).toInt().coerceAtLeast(2.dp)

            val bar = View(this).apply {
                layoutParams = FrameLayout.LayoutParams(barWidth, 12.dp).apply {
                    gravity = Gravity.CENTER_VERTICAL
                }
                background = GradientDrawable().apply {
                    setColor(
                        if (percent == maxPercent)
                            ContextCompat.getColor(this@MonthlyReportActivity, R.color.graph_brown)
                        else
                            ContextCompat.getColor(this@MonthlyReportActivity, R.color.graph_gray)
                    )
                    cornerRadius = 6.dp.toFloat()
                }
            }

            val percentText = TextView(this).apply {
                text = "$label ${percent.toInt()}%"
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(ContextCompat.getColor(this@MonthlyReportActivity, R.color.black))
                typeface = ResourcesCompat.getFont(this@MonthlyReportActivity, R.font.pretendard_semibold)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    leftMargin = barWidth + 8.dp
                }
            }

            val barFrame = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, 32.dp, 1f)
                addView(bar)
                addView(percentText)
            }

            row.addView(emoji)
            row.addView(barFrame)

            emotionRatioContainer.addView(row)
        }
    }

    private fun setupMonthlyEmotionCalendar(dailyStressList: List<DailyStressEntry>) {
        val emojiMap = mapOf(
            "20" to R.drawable.smile,
            "40" to R.drawable.regular,
            "60" to R.drawable.irritated,
            "80" to R.drawable.sad,
            "100" to R.drawable.angry
        )

        val today = LocalDate.now()
        val currentMonth = YearMonth.of(today.year, today.month)
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDay = currentMonth.atDay(1)
        val firstWeekdayIndex = firstDay.dayOfWeek.value % 7

        val emojiByDate = dailyStressList.associateBy({ it.date }) { it.emoji }

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val title = TextView(this).apply {
            text = "${today.year}년 ${today.monthValue}월"
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
            setTextColor(ContextCompat.getColor(this@MonthlyReportActivity, R.color.brown))
            typeface = ResourcesCompat.getFont(this@MonthlyReportActivity, R.font.pretendard_semibold)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16.dp }
        }
        container.addView(title)

        val daysOfWeek = listOf("일", "월", "화", "수", "목", "금", "토")
        val dayHeader = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        for (day in daysOfWeek) {
            val label = TextView(this).apply {
                text = day
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(ContextCompat.getColor(this@MonthlyReportActivity, R.color.gray))
                typeface = ResourcesCompat.getFont(this@MonthlyReportActivity, R.font.pretendard_regular)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            dayHeader.addView(label)
        }
        container.addView(dayHeader)

        val calendarGrid = GridLayout(this).apply {
            rowCount = 6
            columnCount = 7
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = 12.dp }
        }

        for (i in 0 until firstWeekdayIndex) {
            calendarGrid.addView(View(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 23.dp
                    height = 23.dp
                }
            })
        }

        for (i in 1..daysInMonth) {
            val dateStr = currentMonth.atDay(i).toString()
            val emojiKey = emojiByDate[dateStr]
            val emojiRes = emojiMap[emojiKey] ?: R.drawable.comma

            val emojiView = ImageView(this).apply {
                setImageResource(emojiRes)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 23.dp
                    height = 23.dp
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setGravity(Gravity.CENTER)
                    bottomMargin = 12.dp
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            calendarGrid.addView(emojiView)
        }

        container.addView(calendarGrid)
        emotionCalendarContainer.removeAllViews()
        emotionCalendarContainer.addView(container)
    }

    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}