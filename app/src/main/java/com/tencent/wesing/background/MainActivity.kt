package com.tencent.wesing.background

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory


class   MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val shapeId = resources.getDrawable(R.drawable.shape_text_3)
        test()
    }

    private fun test() {
        var startTime = System.nanoTime()
        var xmlDrawable = resources.getDrawable(R.drawable.shape_text_2)
        Log.i("longpo", " no cache xmlDrawable cost time: " + (System.nanoTime() - startTime))

        startTime = System.nanoTime()
        var codeDrawable = TMEBackgroundDrawableFactory.getInstance().createDrawableById(R.drawable.shape_text_2)
        Log.i("longpo", " no cache codeDrawable cost time: " + (System.nanoTime() - startTime))


        startTime = System.nanoTime()
        xmlDrawable = resources.getDrawable(R.drawable.shape_text_2)
        Log.i("longpo", " has cache codeDrawable cost time: " + (System.nanoTime() - startTime))

        startTime = System.nanoTime()
        codeDrawable = TMEBackgroundDrawableFactory.getInstance().createDrawableById(R.drawable.shape_text_2)
        Log.i("longpo", " has cache codeDrawable cost time: " + (System.nanoTime() - startTime))
    }
}