package com.tencent.wesing.test.b.module;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

/**
 * create by zlonghuang on 2021/5/21
 **/
public class TestView extends RelativeLayout {

    public TestView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.b_module_test_layout,this);
    }

}
