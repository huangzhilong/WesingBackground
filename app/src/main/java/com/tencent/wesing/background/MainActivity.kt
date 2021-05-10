package com.tencent.wesing.background

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.tencent.wesing.background.lib.bean.TMEBackgroundMap

class   MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val id = ShapeTest.textId
        val android = ShapeTest.androidId

        val shapeId = resources.getDrawable(R.drawable.shape_text_3)

        test()
    }

    private fun test() {
        val iterable = TMEBackgroundMap.getBackgroundAttributeMap().keys.iterator()
        while (iterable.hasNext()) {
            val key: Int = iterable.next()
            Log.i("longpo", "key: $key  value: ${TMEBackgroundMap.getBackgroundAttributeMap().get(key)}")
        }
    }
}