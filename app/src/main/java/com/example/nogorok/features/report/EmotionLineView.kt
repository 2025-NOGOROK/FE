package com.example.nogorok.features.report

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.nogorok.R

class EmotionLineView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var points: List<Pair<Float, Float>> = emptyList()

    private val paint = Paint().apply {
        color = context.getColor(R.color.graph_brown)
        strokeWidth = 10f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (points.size < 2) return

        val path = Path()
        path.moveTo(points[0].first, points[0].second)
        for (i in 1 until points.size) {
            path.lineTo(points[i].first, points[i].second)
        }
        canvas.drawPath(path, paint)
    }
}
