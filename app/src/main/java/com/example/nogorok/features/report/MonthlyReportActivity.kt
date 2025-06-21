package com.example.nogorok.features.report

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.example.nogorok.R
import java.time.LocalDate
import com.google.android.material.card.MaterialCardView
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.AbsoluteSizeSpan
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan



class MonthlyReportActivity : AppCompatActivity() {

    private lateinit var tvHeader: TextView
    private lateinit var tvDateRange: TextView
    private lateinit var btnBack: ImageView
    private lateinit var mockSummaryContainer: LinearLayout
    private lateinit var stressReportContainer: FrameLayout

    private val viewModel: MonthlyReportViewModel by lazy {
        ViewModelProvider(this)[MonthlyReportViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)

        tvHeader = findViewById(R.id.tvHeader)
        tvDateRange = findViewById(R.id.tvDateRange)
        btnBack = findViewById(R.id.btnBack)
        mockSummaryContainer = findViewById(R.id.mockSummaryContainer)
        stressReportContainer = findViewById(R.id.stressReportContainer)

        tvHeader.text = "월간 리포트"
        tvDateRange.text = getCurrentMonth()

        btnBack.setOnClickListener { finish() }

        setupMockSummary()
        setupStressReport()
    }

    private fun getCurrentMonth(): String {
        val today = LocalDate.now()
        return "${today.year}년 ${today.monthValue}월"
    }

    private fun setupMockSummary() {
        mockSummaryContainer.removeAllViews()

        mockSummaryContainer.addView(makeSummaryCard(
            iconRes = R.drawable.report1,
            message1 = "이번 달에는 짧은 쉼표 일정을",
            highlight = "45개",
            message2 = "수행했어요!",
            message3 = "2025년 5월 대비 4개 더 수행했어요."
        ))

        mockSummaryContainer.addView(makeSummaryCard(
            iconRes = R.drawable.report2,
            message1 = "이번 달에는 긴 쉼표 일정을",
            highlight = "20개",
            message2 = "수행했어요!",
            message3 = "2025년 5월 대비 2개 더 수행했어요."
        ))
    }

    private fun setupStressReport() {
        stressReportContainer.removeAllViews()

        // ✅ 자식이 삐져나와도 보이게 설정
        stressReportContainer.clipChildren = false
        stressReportContainer.clipToPadding = false

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            clipChildren = false   // ✅ 추가
            clipToPadding = false  // ✅ 추가
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val stressRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
            clipChildren = false   // ✅ 추가
            clipToPadding = false  // ✅ 추가
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 24.dp   // ✅ 이모지가 위로 올라갈 수 있도록 충분한 margin 확보
                bottomMargin = 24.dp
            }
        }

        // 카드 2개 (이모지 반쯤 올라가도록)
        stressRow.addView(makeStressBox(R.drawable.angry, "스트레스가 가장 많았던 날", "5월 4일", "1개"))
        stressRow.addView(makeStressBox(R.drawable.smile, "스트레스가 가장 적었던 날", "5월 19일", "4개"))
        container.addView(stressRow)

        // 평균 카드들
        val avgRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        avgRow.addView(makeAverageBox(R.drawable.regular, "이달의 하루 평균 스트레스 지수", "112"))
        avgRow.addView(makeAverageBox(R.drawable.comma, "이달의 하루 평균 쉼표 개수", "2.5개"))
        container.addView(avgRow)

        stressReportContainer.addView(container)
    }


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

            // 내부 레이아웃 (LinearLayout)
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                // 상단 텍스트
                addView(TextView(context).apply {
                    text = message1
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                })

                addView(TextView(context).apply {
                    val fullText = "총 $highlight $message2"
                    val spannable = SpannableString(fullText)

                    val highlightStart = fullText.indexOf(highlight)
                    val highlightEnd = highlightStart + highlight.length

                    val message2Start = fullText.indexOf(message2)
                    val message2End = message2Start + message2.length

                    // ✅ highlight ("45개" 등): Pretendard SemiBold, 18sp
                    spannable.setSpan(
                        AbsoluteSizeSpan(18, true),
                        highlightStart,
                        highlightEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.pretendard_semibold)!!),
                        highlightStart,
                        highlightEnd,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // ✅ message2 ("수행했어요!" 등): Pretendard Regular, 16sp
                    spannable.setSpan(
                        AbsoluteSizeSpan(16, true),
                        message2Start,
                        message2End,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    spannable.setSpan(
                        CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.pretendard_regular)!!),
                        message2Start,
                        message2End,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    text = spannable
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // 전체 기본 사이즈
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                })



                // 이미지
                addView(ImageView(context).apply {
                    setImageResource(iconRes)
                    layoutParams = LinearLayout.LayoutParams(164.dp, 164.dp).apply {
                        gravity = Gravity.CENTER_HORIZONTAL
                        topMargin = 12.dp
                        bottomMargin = 12.dp
                    }
                })

                // 하단 메시지
                addView(TextView(context).apply {
                    val spannable = SpannableString(message3)
                    val highlights = listOf("4개 더", "2개 더")

                    for (highlightText in highlights) {
                        val start = message3.indexOf(highlightText)
                        if (start >= 0) {
                            spannable.setSpan(
                                ForegroundColorSpan(Color.parseColor("#73605A")),
                                start,
                                start + highlightText.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }

                    text = spannable
                    gravity = Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                })

            })
        }
    }

    private fun makeStressBox(iconRes: Int, label: String, date: String, count: String): FrameLayout {
        val iconSize = 32.dp
        val iconOverlap = iconSize / 2

        return FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginStart = 8.dp
                marginEnd = 8.dp
            }

            // ✅ 자식 뷰가 삐져나와도 자르지 않게 설정
            clipChildren = false
            clipToPadding = false

            // ✅ 카드 내부
            val cardContent = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                background = ContextCompat.getDrawable(context, R.drawable.bg_stress_card)
                setPadding(20.dp, iconOverlap + 12.dp, 20.dp, 20.dp)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )

                val fullText = "$label\n${date}에\n쉼표 일정 $count 수행했어요."
                val spannable = SpannableString(fullText)

                val dateStart = fullText.indexOf(date)
                spannable.setSpan(
                    CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.pretendard_bold)!!),
                    dateStart, dateStart + date.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                val countStart = fullText.indexOf(count)
                spannable.setSpan(
                    CustomTypefaceSpan(ResourcesCompat.getFont(context, R.font.pretendard_bold)!!),
                    countStart, countStart + count.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                addView(TextView(context).apply {
                    text = spannable
                    gravity = Gravity.CENTER
                    setLineSpacing(6.dpF, 1.0f)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, 12.dpF)
                    setTextColor(Color.parseColor("#5C4F47"))
                    typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
                })
            }

            // ✅ 이모지 아이콘 (카드 밖으로 반쯤 올라감)
            val iconView = ImageView(context).apply {
                setImageResource(iconRes)
                layoutParams = FrameLayout.LayoutParams(iconSize, iconSize, Gravity.TOP or Gravity.CENTER_HORIZONTAL).apply {
                    topMargin = -iconOverlap
                }
            }

            addView(cardContent)
            addView(iconView)
        }
    }




    private fun makeAverageBox(iconRes: Int, title: String, value: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            // 타이틀 텍스트 (위쪽)
            addView(TextView(context).apply {
                text = title
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(Color.parseColor("#1C1C1C"))  // 필요 시 색상 지정
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_regular)
            })

            // 이모지 + 값 텍스트 나란히
            val row = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 6.dp
                }
            }

            // 이모지 이미지
            row.addView(ImageView(context).apply {
                setImageResource(iconRes)
                layoutParams = LinearLayout.LayoutParams(28.dp, 28.dp).apply {
                    marginEnd = 6.dp
                }
            })

            // 숫자 텍스트
            row.addView(TextView(context).apply {
                text = value
                gravity = Gravity.CENTER
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                setTextColor(Color.parseColor("#5C4F47"))
                typeface = ResourcesCompat.getFont(context, R.font.pretendard_semibold)
            })

            addView(row)
        }
    }


    class CustomTypefaceSpan(private val customTypeface: Typeface) : TypefaceSpan("") {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, customTypeface)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeFace(paint, customTypeface)
        }

        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle = paint.typeface?.style ?: 0
            val fake = oldStyle and tf.style.inv()

            if (fake and Typeface.BOLD != 0) {
                paint.isFakeBoldText = true
            }
            if (fake and Typeface.ITALIC != 0) {
                paint.textSkewX = -0.25f
            }

            paint.typeface = tf
        }
    }


    val Int.dp: Int get() = (this * resources.displayMetrics.density).toInt()
    val Int.dpF: Float get() = this * resources.displayMetrics.density

}