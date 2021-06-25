package com.tencent.wesing.background;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory;

/**
 * create by zlonghuang on 2021/6/11
 **/
public class SecondActivity extends Activity {

    private static final String TAG = "SecondActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_main);


        getResources().getDrawable(R.drawable.shape_text_1);


        TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.shape_text_1);


        // 测速
        long startTime = System.nanoTime();
        Drawable xmlDrawable = TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.shape_text_2);
        Log.i("TMEBackgroundMap", " no cache xmlDrawable cost time: " + (System.nanoTime() - startTime));

        startTime = System.nanoTime();
        Drawable codeDrawable = TMEBackgroundDrawableFactory.createDrawableById(R.drawable.shape_text_2);
        Log.i("TMEBackgroundMap", " no cache codeDrawable cost time: " + (System.nanoTime() - startTime));
    }
}
