package com.tencent.wesing.background

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.tencent.wesing.background.lib.TMEBackgroundContext
import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory


class  MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "longpo"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_btn).setOnClickListener {
            jump()
        }
        findViewById<View>(R.id.tv_btn_test).setOnClickListener {
            test()
        }
        resources.getDrawable(R.drawable.shape_text_1)
        TMEBackgroundContext.getContext().resources.getDrawable(R.drawable.shape_text_1)
    }

    private fun jump() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }

    private fun test() {
        var startTime = System.nanoTime()
        var xmlDrawable = resources.getDrawable(R.drawable.shape_text_2)
        Log.i(TAG, " no cache xmlDrawable cost time: " + (System.nanoTime() - startTime))

        startTime = System.nanoTime()
        var codeDrawable = TMEBackgroundDrawableFactory.createDrawableById(R.drawable.shape_text_2)
        Log.i(TAG, " no cache codeDrawable cost time: " + (System.nanoTime() - startTime))

    }
}