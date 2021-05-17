package com.tencent.wesing.background

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tencent.wesing.background.lib.TMEBackgroundFactory
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap
import com.tencent.wesing.background.lib.drawable.ShapeDrawableFactory

class   MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val shapeId = resources.getDrawable(R.drawable.shape_text_3)

        ShapeDrawableFactory.getInstance()

        test()
    }

    private fun test() {
        val iterable = TMEBackgroundMap.getBackgroundAttributeMap().keys.iterator()
        while (iterable.hasNext()) {
            val key: Int = iterable.next()
            Log.i("longpo", "key: $key  value: ${TMEBackgroundMap.getBackgroundAttributeMap().get(key)}")
        }

        var startTime = System.nanoTime()
        val xmlDrawable = resources.getDrawable(R.drawable.shape_text_4)
        Log.i("longpo", "xmlDrawable cost time: " + (System.nanoTime() - startTime))

        startTime = System.nanoTime()
        val codeDrawable = ShapeDrawableFactory.getInstance().createDrawableById(R.drawable.shape_text_4)
        Log.i("longpo", "codeDrawable cost time: " + (System.nanoTime() - startTime))
    }
}