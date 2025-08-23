package com.example.nogorok.features.report

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import com.example.nogorok.databinding.ActivityMonthlyReportBinding
import com.google.android.material.card.MaterialCardView
import java.time.LocalDate

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
        binding.tvDateRange.text = currentMonthLabel()
        binding.btnBack.setOnClickListener { finish() }

        setupMockSummary()
        setupStressReport()
    }

    private fun currentMonthLabel(): String {
        val today = LocalDate.now()
        return "${today.year}년 ${today.monthValue}월"
    }

    /** 상단 ‘이달의 쉼표’ 카드 2개 (정적 + 하이라이트만 스팬 처리) */
    private fun setupMockSummary() = with(binding) {
        mockSummaryContainer.removeAllViews()

        mockSummaryContainer.addView(
            makeSummaryCard(
                iconRes = R.drawable.report1,
                message1 = "이번 달에는 짧은 쉼표 일정을",
                highlight = "45개",
                message2 = "수행했어요!",
                message3 = "지난달 대비 4개 더 수행했어요."
            )
        )
        mockSummaryContainer.addView(
            makeSummaryCard(
                iconRes = R.drawable.report2,
                message1 = "이번 달에는 긴 쉼표 일정을",
                highlight = "20개",
                message2 = "수행했어요!",
                message3 = "지난달 대비 2개 더 수행했어요."
            )
        )
    }

    /** 중간 ‘스트레스와 쉼표 리포트’ — XML 카드 텍스트 채우기 */
    private fun setupStressReport() = with(binding) {
        tvMostStressDesc.text = "스트레스가 가장 많았던 날 5월 4일에\n쉼표 일정을 1개 수행했어요."
        tagMost1.text = "독서하기"
        timeMost1.text = "20:00-20:30"

        tvLeastStressDesc.text = "스트레스가 가장 적었던 날 5월 7일에\n쉼표 일정을 2개 수행했어요."
        tagLeast1.text = "독서하기"
        tagLeast2.text = "경복궁 야간개장"
        timeLeast2.text = "21:00-22:30"
    }

    // ---------------------- 카드 빌더들 (상단 요약 카드) ----------------------

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
                    val message3Span = SpannableString(message3)
                    val key = "더"
                    val idx = message3.indexOf(key)
                    if (idx >= 0) {
                        message3Span.setSpan(
                            ForegroundColorSpan(Color.parseColor("#73605A")),
                            idx, idx + key.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    text = message3Span
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTextColor(Color.parseColor("#1C1C1C"))
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                })
            })
        }
    }

    // ---------------------- 유틸 ----------------------

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
    private val Int.dpF: Float get() = this * resources.displayMetrics.density
}
