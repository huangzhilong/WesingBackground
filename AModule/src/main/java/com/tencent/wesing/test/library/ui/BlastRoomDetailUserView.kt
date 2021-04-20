package com.tencent.wesing.test.library.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tencent.wesing.test.library.R

/**
 * create by zlonghuang on 2020/6/19
 **/
class BlastRoomDetailUserView @JvmOverloads constructor(context: Context, val attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    private var vUserHeader: ImageView? = null
    private var tvCount: TextView? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_blast_room_detail_user, this)
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        vUserHeader = findViewById(R.id.user_header)
        vUserHeader?.setImageResource(R.drawable.icon_blast_room_user_empty_state)
        tvCount = findViewById(R.id.tv_gift_count)
        tvCount?.text = "0"
    }
}