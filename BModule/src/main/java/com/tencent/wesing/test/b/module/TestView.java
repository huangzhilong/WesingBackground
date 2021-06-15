package com.tencent.wesing.test.b.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.tencent.wesing.background.lib.TMEBackgroundContext;

/**
 * create by zlonghuang on 2021/5/21
 **/
public class TestView extends RelativeLayout {

    public TestView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.b_module_test_layout,this);
        TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.test_text_1_b);
    }

}
