package com.tencent.wesing.background

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.tencent.wesing.background.lib.TMEBackgroundContext


class  MainActivity : Activity() {

    companion object {
        const val TAG = "longpo"
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

        theme.resources.getDrawable(R.drawable.shape_text_1)
    }

    private fun jump() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivity(intent)
    }

    private fun test() {

    }
}