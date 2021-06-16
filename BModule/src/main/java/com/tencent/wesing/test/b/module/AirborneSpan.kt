package com.wesing.module_partylive_common.im.publicscreen.ui.widget

import android.graphics.*
import android.text.style.ReplacementSpan
import android.util.TypedValue
import com.tencent.wesing.background.lib.TMEBackgroundContext

/**
 * create by zlonghuang on 2021/4/27
 **/

class AirborneSpan(text: String): ReplacementSpan() {

    private var content = text
    private var mTextSize: Float = 0f
    private val mTextColor: Int = Color.parseColor("#ffffff")
    private val mBgRadius: Int
    private var mTextWidth: Int = 0
    private var mTextHeight: Int = 0
    private var mBgWidth: Int = 0
    private var mBgHeight: Int = 0
    private var mTextPaint: Paint
    private var mBgPaint: Paint

    init {
        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11f, TMEBackgroundContext.getContext().getResources().displayMetrics)

        mTextPaint = Paint()
        mTextPaint.textSize = mTextSize
        mTextPaint.color = mTextColor
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.LEFT
        mTextPaint.typeface = Typeface.DEFAULT_BOLD
        val textRect = Rect()
        mTextPaint.getTextBounds(content, 0, content.length, textRect)
        mTextWidth = textRect.width()
        mTextHeight = textRect.height()

        mBgPaint = Paint()
        mBgPaint.color = Color.parseColor("#433ff1")
        mBgPaint.style = Paint.Style.FILL
        mBgPaint.isAntiAlias = true

        mBgWidth = mTextWidth + 11
        mBgHeight = mTextHeight + 23

        mBgRadius = 33
    }



    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return mBgWidth
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val metrics = paint.fontMetrics
        val textHeight = metrics.descent - metrics.ascent
        val bgStartY = y + (textHeight - mBgHeight) / 2 + metrics.ascent - 2

        //背景
        val rectBgF = RectF(x, bgStartY, x + mBgWidth, bgStartY + mBgHeight)
        canvas.drawRoundRect(rectBgF, mBgRadius.toFloat(), mBgRadius.toFloat(), mBgPaint)

        //文字
        val mTextMetrics = mTextPaint.fontMetrics
        val mLevelTextHeight = mTextMetrics.bottom - mTextMetrics.top
        val mTextX = 43
        val mTextY = rectBgF.top + (mBgHeight - mLevelTextHeight) / 2 - mTextMetrics.top
        canvas.drawText(content, x + mTextX, mTextY, mTextPaint)
    }
}