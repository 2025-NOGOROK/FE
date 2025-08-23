package com.example.nogorok.features.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class StressLineChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#73605A")
        strokeWidth = dp(3f)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val pointFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
    private val pointStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#73605A"); strokeWidth = dp(2f); style = Paint.Style.STROKE }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#73605A"); textSize = dp(12f) }
    private val axisPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#2E2A28"); strokeWidth = dp(2f); style = Paint.Style.STROKE }

    private var values: List<Float> = emptyList()
    private var xLabels: List<String> = emptyList()

    private val path = Path()
    private val textBounds = Rect()
    private val axisAreaH = dp(36f)
    private val pointR = dp(7f)

    init {
        if (paddingLeft == 0 && paddingRight == 0 && paddingTop == 0 && paddingBottom == 0) {
            setPadding(dp(22f).toInt(), dp(22f).toInt(), dp(22f).toInt(), dp(44f).toInt())
        }
    }

    fun setData(values: List<Float>, labels: List<String>) {
        this.values = values
        this.xLabels = labels
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty() || xLabels.isEmpty()) return

        val left = paddingLeft.toFloat()
        val right = width - paddingRight.toFloat()
        val top = paddingTop.toFloat()
        val bottom = height - paddingBottom.toFloat()

        val plotTop = top
        val plotBottom = bottom - axisAreaH
        val plotH = (plotBottom - plotTop).coerceAtLeast(1f)
        val plotW = (right - left).coerceAtLeast(1f)

        val minV = values.minOrNull() ?: 0f
        val maxV = values.maxOrNull() ?: 1f
        val range = max(1f, maxV - minV)

        fun x(i: Int): Float {
            // ✅ 점이 1개면 가운데에 배치
            return if (values.size == 1) (left + right) / 2f
            else left + (plotW) * (i / (values.size - 1f))
        }
        fun y(v: Float): Float {
            val norm = (v - minV) / range
            val topSafe = plotTop + pointR + linePaint.strokeWidth
            val bottomSafe = plotBottom - pointR - linePaint.strokeWidth
            val raw = plotBottom - norm * plotH
            return min(max(raw, topSafe), bottomSafe)
        }

        // 라인
        if (values.size >= 2) {
            path.reset()
            path.moveTo(x(0), y(values[0]))
            for (i in 1 until values.size) path.lineTo(x(i), y(values[i]))
            canvas.drawPath(path, linePaint)
        } else {
            // 값 1개면 축 위에 포인트만 보이도록 라인은 생략(원한다면 짧은 가이드 라인을 그려도 됨)
        }

        // 포인트 + 값 라벨
        for (i in values.indices) {
            val cx = x(i)
            val cy = y(values[i])
            canvas.drawCircle(cx, cy, pointR, pointFill)
            canvas.drawCircle(cx, cy, pointR, pointStroke)

            val label = values[i].toInt().toString()
            labelPaint.getTextBounds(label, 0, label.length, textBounds)
            val tx = cx - textBounds.width() / 2f
            val ty = cy - pointR - dp(6f)
            canvas.drawText(label, tx, ty, labelPaint)
        }

        // X축
        val axisY = plotBottom + dp(8f)
        canvas.drawLine(left + dp(14f), axisY, right - dp(14f), axisY, axisPaint)

        // ✅ X라벨 안전 그리기
        val n = xLabels.size
        labelPaint.color = Color.parseColor("#2E2A28")
        when (n) {
            1 -> {
                // 가운데 한 개
                val cx = (left + right) / 2f
                drawCenteredText(canvas, xLabels[0], cx, bottom - dp(8f), labelPaint)
            }
            2 -> {
                // 양 끝 두 개
                val cx0 = left
                val cx1 = right
                drawCenteredText(canvas, xLabels[0], cx0 + dp(14f), bottom - dp(8f), labelPaint, clampToLeft = true)
                drawCenteredText(canvas, xLabels[1], cx1 - dp(14f), bottom - dp(8f), labelPaint, clampToRight = true)
                // (중앙 눈금은 생략)
            }
            else -> {
                val per = plotW / (n - 1f)
                for (i in 0 until n) {
                    val cx = left + per * i
                    drawCenteredText(canvas, xLabels[i], cx, bottom - dp(8f), labelPaint)
                }
                // 중앙 눈금(선택)
                val midX = left + per * 1
                canvas.drawLine(midX, axisY - dp(8f), midX, axisY + dp(1f), axisPaint)
            }
        }
    }

    private fun drawCenteredText(
        canvas: Canvas,
        text: String,
        cx: Float,
        y: Float,
        paint: Paint,
        clampToLeft: Boolean = false,
        clampToRight: Boolean = false
    ) {
        paint.getTextBounds(text, 0, text.length, textBounds)
        var tx = cx - textBounds.width() / 2f
        if (clampToLeft) tx = cx - textBounds.width()          // 왼쪽 끝 붙이기
        if (clampToRight) tx = cx - 0f                          // 오른쪽 끝 붙이기
        canvas.drawText(text, tx, y, paint)
    }

    private fun dp(v: Float) = v * resources.displayMetrics.density
}
