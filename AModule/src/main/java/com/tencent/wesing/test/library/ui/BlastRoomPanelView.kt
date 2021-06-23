package com.tencent.wesing.test.library.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.tencent.wesing.test.library.R

/**
 * create by zlonghuang on 2020/6/19
 **/
class BlastRoomPanelView @JvmOverloads constructor(context: Context, val attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayout(context, attrs, defStyle) {

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_blast_room_detail_panel, this)
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        resources.getDrawable(R.drawable.test_text_1)
        context.resources.getDrawable(R.drawable.test_text_1)

    }
}