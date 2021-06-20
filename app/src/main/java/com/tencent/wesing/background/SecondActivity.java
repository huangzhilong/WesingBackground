package com.tencent.wesing.background;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.wesing.background.lib.TMEBackgroundContext;

/**
 * create by zlonghuang on 2021/6/11
 **/
public class SecondActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_main);

        //这个不行
        getResources().getDrawable(R.drawable.shape_text_1);


        //这个可以
        TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.shape_text_1);
    }
}
