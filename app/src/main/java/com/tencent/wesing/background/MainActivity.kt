package com.tencent.wesing.background

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory


class  MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_btn).setOnClickListener {
            jump()
        }
        window.decorView.postDelayed(object : Runnable {
            override fun run() {
                test()
            }
        }, 5000)
    }

    private fun jump() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }

    private fun test() {
        var startTime = System.nanoTime()
        var xmlDrawable = resources.getDrawable(R.drawable.shape_text_2)
        Log.i("longpo", " no cache xmlDrawable cost time: " + (System.nanoTime() - startTime))

        startTime = System.nanoTime()
        var codeDrawable = TMEBackgroundDrawableFactory.createDrawableById(R.drawable.shape_text_2)
        Log.i("longpo", " no cache codeDrawable cost time: " + (System.nanoTime() - startTime))

    }
}