package com.example.neuralsight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.LinkedList
import kotlin.math.max

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<Detection> = LinkedList<Detection>()
    private var boxPaint = Paint()
    private var textBackgroundPaint = Paint()
    private var textPaint = Paint()

    private var scaleFactor: Float = 1f

    init {
        initPaints()
    }

    private fun initPaints() {
        // අකුරු වල පසුබිම කළු පාටින්
        textBackgroundPaint.color = Color.BLACK
        textBackgroundPaint.style = Paint.Style.FILL
        textBackgroundPaint.textSize = 50f

        // අකුරු සුදු පාටින්
        textPaint.color = Color.WHITE
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 50f

        // කොටුව කොළ පාටින් සහ ටිකක් මහතට (8F)
        boxPaint.color = Color.GREEN
        boxPaint.strokeWidth = 8F
        boxPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            val top = boundingBox.top * scaleFactor
            val bottom = boundingBox.bottom * scaleFactor
            val left = boundingBox.left * scaleFactor
            val right = boundingBox.right * scaleFactor

            // වස්තුව වටේට කොටුව අඳිමු
            val drawableRect = RectF(left, top, right, bottom)
            canvas.drawRect(drawableRect, boxPaint)

            // නම සහ විශ්වාසනීයත්ව ප්‍රතිශතය (උදා: "cup 85.5%")
            val category = result.categories[0]
            val text = "${category.label} " + String.format("%.2f", category.score * 100) + "%"

            // නම පිටිපස්සේ කළු පසුබිම සහ සුදු අකුරු අඳිමු
            textBackgroundPaint.getTextBounds(text, 0, text.length, android.graphics.Rect())
            canvas.drawRect(
                left,
                top,
                left + textPaint.measureText(text) + 8,
                top + textPaint.textSize + 8,
                textBackgroundPaint
            )
            canvas.drawText(text, left, top + textPaint.textSize, textPaint)
        }
    }

    // AI එකෙන් දෙන අලුත් ප්‍රතිඵල මේකට දාන function එක
    fun setResults(detectionResults: MutableList<Detection>, imageWidth: Int, imageHeight: Int) {
        results = detectionResults
        // කැමරාවේ රූපය සහ අපේ තිරයේ ප්‍රමාණය සසඳලා scale කරගැනීම
        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)
        invalidate() // අලුත් දත්ත ආවාම ආයෙත් තිරයේ කොටු අඳින්න කියලා කියනවා
    }
}