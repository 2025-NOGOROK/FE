package com.example.nogorok.features.report

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.databinding.ActivityMonthlyReportBinding
import com.example.nogorok.network.dto.CommaEvent
import com.example.nogorok.network.dto.MonthlyCountsDto
import com.example.nogorok.network.dto.MonthlyStressDto
import com.example.nogorok.network.dto.StressfulDay
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class MonthlyReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthlyReportBinding
    private val viewModel: MonthlyReportViewModel by lazy {
        ViewModelProvider(this)[MonthlyReportViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonthlyReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvHeader.text = "월간 리포트"
        binding.btnBack.setOnClickListener { finish() }

        viewModel.load()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.counts.observe(this) { dto ->
            dto ?: return@observe
            binding.tvDateRange.text = "${dto.year}년 ${dto.month}월"
            renderMonthlySummary(dto)
        }

        viewModel.stress.observe(this) { dto ->
            dto ?: return@observe
            renderStressCards(dto)
        }

        // ✅ 차트 연결: trend 관찰
        viewModel.trend.observe(this) {
            val (values, labels) = viewModel.trendAsChartData()
            if (values.isNotEmpty() && labels.isNotEmpty()) {
                binding.stressChart.setData(values, labels)
            } else {
                // 데이터 없을 때는 기본값 비우기(옵션)
                binding.stressChart.setData(emptyList(), emptyList())
            }
        }
    }

    // ---------------------- monthly 카드 ----------------------

    private fun renderMonthlySummary(dto: MonthlyCountsDto) = with(binding) {
        mockSummaryContainer.removeAllViews()

        val prev = YearMonth.of(dto.year, dto.month).minusMonths(1)
        val shortMsg3 = "${prev.year}년 ${prev.monthValue}월 대비 ${diffText(dto.shortDiffFromPrev)} 수행했어요."
        val longMsg3  = "${prev.year}년 ${prev.monthValue}월 대비 ${diffText(dto.longDiffFromPrev)} 수행했어요."

        mockSummaryContainer.addView(
            makeSummaryCard(
                iconRes = R.drawable.report1,
                message1 = "이번 달에는 짧은 쉼표 일정을",
                highlight = "${dto.shortCount}개",
                message2 = "수행했어요!",
                message3 = shortMsg3
            )
        )
        mockSummaryContainer.addView(
            makeSummaryCard(
                iconRes = R.drawable.report2,
                message1 = "이번 달에는 긴 쉼표 일정을",
                highlight = "${dto.longCount}개",
                message2 = "수행했어요!",
                message3 = longMsg3
            )
        )
    }

    private fun diffText(diff: Int): String = when {
        diff > 0  -> "${diff}개 더"
        diff < 0  -> "${abs(diff)}개 덜"
        else      -> "변화 없이"
    }

    // ---------------------- monthly-stress 카드 ----------------------

    private fun renderStressCards(dto: MonthlyStressDto) = with(binding) {
        // ----- 가장 스트레스 많았던 날 -----
        dto.mostStressful?.let { day ->
            val (m, d) = parseMonthDay(day.date)
            val declaredCnt = (day.shortCount ?: 0) + (day.longCount ?: 0) + (day.emergencyCount ?: 0)
            val totalCnt = if (declaredCnt > 0) declaredCnt
            else day.shortEvents.orEmpty().size + day.longEvents.orEmpty().size + day.emergencyEvents.orEmpty().size
            tvMostStressDesc.text = "스트레스가 가장 많았던 날 ${m}월 ${d}일에\n쉼표 일정을 ${totalCnt}개 수행했어요."

            mostPillList.removeAllViews()
            labeledEvents(day).forEachIndexed { idx, item ->
                val row = buildPillRow(
                    kind = item.kind,
                    title = item.event.title.orEmpty(),
                    time = hhmmRange(item.event.startTime, item.event.endTime)
                )
                (row.layoutParams as LinearLayout.LayoutParams).apply {
                    if (idx > 0) topMargin = 10.dp
                }
                mostPillList.addView(row)
            }
        }

        // ----- 가장 스트레스 적었던 날 -----
        val least = viewModel.leastStressfulDay()
        if (least != null) {
            val (m, d) = parseMonthDay(least.date)
            val declaredCnt = (least.shortCount ?: 0) + (least.longCount ?: 0) + (least.emergencyCount ?: 0)
            val totalCnt = if (declaredCnt > 0) declaredCnt
            else least.shortEvents.orEmpty().size + least.longEvents.orEmpty().size + least.emergencyEvents.orEmpty().size
            tvLeastStressDesc.text = "스트레스가 가장 적었던 날 ${m}월 ${d}일에\n쉼표 일정을 ${totalCnt}개 수행했어요."

            leastPillList.removeAllViews()
            labeledEvents(least).forEachIndexed { idx, item ->
                val row = buildPillRow(
                    kind = item.kind,
                    title = item.event.title.orEmpty(),
                    time = hhmmRange(item.event.startTime, item.event.endTime)
                )
                (row.layoutParams as LinearLayout.LayoutParams).apply {
                    if (idx > 0) topMargin = 10.dp
                }
                leastPillList.addView(row)
            }
        } else {
            tvLeastStressDesc.text = "스트레스가 가장 적었던 날 정보를 불러오지 못했어요."
            leastPillList.removeAllViews()
        }
    }

    // ---------------------- 라벨링 & 칩 빌더 ----------------------

    private enum class EventKind { SHORT, LONG, EMERGENCY }
    private data class LabeledEvent(val event: CommaEvent, val kind: EventKind)

    private fun labeledEvents(day: StressfulDay): List<LabeledEvent> {
        val list = mutableListOf<LabeledEvent>()
        day.shortEvents.orEmpty().forEach     { list += LabeledEvent(it, EventKind.SHORT) }
        day.emergencyEvents.orEmpty().forEach { list += LabeledEvent(it, EventKind.EMERGENCY) }
        day.longEvents.orEmpty().forEach      { list += LabeledEvent(it, EventKind.LONG) }
        return list.sortedBy { tryParseLocalTime(it.event.startTime) ?: LocalTime.MIN }
    }

    private fun buildPillRow(kind: EventKind, title: String, time: String): LinearLayout {
        val bg = when (kind) {
            EventKind.LONG -> R.drawable.bg_pill_outline_mint
            EventKind.SHORT, EventKind.EMERGENCY -> R.drawable.bg_pill_brown_filled
        }
        val txtColor = when (kind) {
            EventKind.LONG -> Color.parseColor("#5B463E")
            EventKind.SHORT, EventKind.EMERGENCY -> Color.WHITE
        }

        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundResource(bg)
            minimumHeight = 44.dp
            setPadding(14.dp, 8.dp, 14.dp, 8.dp)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            addView(TextView(context).apply {
                text = title
                setTextColor(txtColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_semibold)
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
            })

            addView(View(context).apply {
                layoutParams = LinearLayout.LayoutParams(0, 0, 1f)
            })

            addView(TextView(context).apply {
                text = time
                setTextColor(txtColor)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_semibold)
            })
        }
    }

    // ---------------------- 공통 유틸 ----------------------

    private fun parseMonthDay(isoDate: String?): Pair<Int, Int> = try {
        val d = LocalDate.parse(isoDate)
        d.monthValue to d.dayOfMonth
    } catch (_: Throwable) {
        val t = LocalDate.now(); t.monthValue to t.dayOfMonth
    }

    private fun tryParseLocalTime(text: String?): LocalTime? = try {
        if (text.isNullOrBlank()) null else LocalTime.parse(text)
    } catch (_: Throwable) { null }

    private fun hhmmRange(start: String?, end: String?): String {
        val fmt = DateTimeFormatter.ofPattern("HH:mm")
        val s = tryParseLocalTime(start)?.format(fmt) ?: (start ?: "")
        val e = tryParseLocalTime(end)?.format(fmt) ?: (end ?: "")
        return if (s.isBlank() && e.isBlank()) "-" else "$s-$e"
    }

    // ---------------------- 요약 카드 빌더 ----------------------

    private fun makeSummaryCard(
        iconRes: Int,
        message1: String,
        highlight: String,
        message2: String,
        message3: String
    ): MaterialCardView {
        return MaterialCardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(326.dp, 326.dp).apply {
                topMargin = 12.dp
                bottomMargin = 12.dp
                gravity = Gravity.CENTER_HORIZONTAL
            }
            radius = 16.dp.toFloat()
            cardElevation = 14.dp.toFloat()
            setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            setContentPadding(20.dp, 20.dp, 20.dp, 20.dp)
            useCompatPadding = true

            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                addView(TextView(context).apply {
                    text = message1
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                    setTextColor(Color.parseColor("#1C1C1C"))
                })

                addView(TextView(context).apply {
                    val full = "총 $highlight $message2"
                    val span = SpannableString(full)

                    val hStart = full.indexOf(highlight)
                    val hEnd = hStart + highlight.length
                    span.setSpan(AbsoluteSizeSpan(18, true), hStart, hEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    span.setSpan(
                        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.pretendard_semibold)!!),
                        hStart, hEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    val m2Start = full.indexOf(message2)
                    val m2End = m2Start + message2.length
                    span.setSpan(AbsoluteSizeSpan(16, true), m2Start, m2End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    span.setSpan(
                        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.pretendard_regular)!!),
                        m2Start, m2End, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    text = span
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                    setTextColor(Color.parseColor("#1C1C1C"))
                })

                addView(ImageView(context).apply {
                    setImageResource(iconRes)
                    layoutParams = LinearLayout.LayoutParams(164.dp, 164.dp).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                        topMargin = 12.dp
                        bottomMargin = 12.dp
                    }
                })

                addView(TextView(context).apply {
                    val span = SpannableString(message3)
                    val key = "더"
                    val idx = message3.indexOf(key)
                    if (idx >= 0) {
                        span.setSpan(
                            ForegroundColorSpan(Color.parseColor("#73605A")),
                            idx, idx + key.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    text = span
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTextColor(Color.parseColor("#1C1C1C"))
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                })
            })
        }
    }

    class CustomTypefaceSpan(private val customTypeface: Typeface) : TypefaceSpan("") {
        override fun updateDrawState(ds: TextPaint) = apply(ds, customTypeface)
        override fun updateMeasureState(paint: TextPaint) = apply(paint, customTypeface)
        private fun apply(paint: Paint, tf: Typeface) {
            val old = paint.typeface?.style ?: 0
            val fake = old and tf.style.inv()
            if (fake and Typeface.BOLD != 0) paint.isFakeBoldText = true
            if (fake and Typeface.ITALIC != 0) paint.textSkewX = -0.25f
            paint.typeface = tf
        }
    }

    private val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
}
