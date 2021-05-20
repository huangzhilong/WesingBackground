package com.tencent.wesing.background

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap
import com.tencent.wesing.background.lib.drawable.GradientDrawableFactory

class   MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val shapeId = resources.getDrawable(R.drawable.shape_text_3)

        GradientDrawableFactory.getInstance()

        test()
    }

    private fun test() {
        var startTime = System.nanoTime()
        val xmlDrawable = resources.getDrawable(R.drawable.shape_text_4)
        Log.i("longpo", "xmlDrawable cost time: " + (System.nanoTime() - startTime))

        startTime = System.nanoTime()
        val codeDrawable = GradientDrawableFactory.getInstance().createDrawableById(R.drawable.shape_text_4)
        Log.i("longpo", "codeDrawable cost time: " + (System.nanoTime() - startTime))
    }
}