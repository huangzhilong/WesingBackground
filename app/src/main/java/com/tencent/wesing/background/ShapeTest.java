package com.tencent.wesing.background;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.tencent.wesing.background.lib.TMEBackgroundContext;
import com.tencent.wesing.background.lib.bean.GradientDrawableInfo;
import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory;
import com.tencent.wesing.test.library.AppInfo;

import java.util.HashMap;

/**
 * create by zlonghuang on 2021/4/20
 **/

class ShapeTest {

    private GradientDrawableInfo info = new GradientDrawableInfo();
    private AppInfo appInfo = new AppInfo();

    private Context mContext = TMEBackgroundContext.getContext();
    public Drawable drawable = TMEBackgroundContext.getContext().getResources().getDrawable(R.drawable.ic_launcher);
    public Drawable drawableA = mContext.getResources().getDrawable(R.drawable.ic_launcher);


    public Drawable drawable1 = ContextCompat.getDrawable(TMEBackgroundContext.getContext(), R.drawable.ic_launcher);
    public Drawable drawable1A = ContextCompat.getDrawable(mContext, R.drawable.ic_launcher);

    private ImageView imageView = new ImageView(mContext);


    public void testDrawable() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable1 = TMEBackgroundContext.getContext().getResources()
                    .getDrawable(R.drawable.ic_launcher, TMEBackgroundContext.getContext().getTheme());
            Drawable test1 = mContext.getDrawable(R.drawable.shape_ring);
            Drawable test1A = TMEBackgroundContext.getContext().getDrawable(R.drawable.shape_ring);
        }
        Drawable drawable1k = TMEBackgroundContext.getContext()
                .getResources()
                .getDrawable(R.drawable.ic_launcher);
        Drawable drawable2 = ContextCompat
                .getDrawable(mContext, R.drawable.ic_launcher);
        Drawable drawable3 = ContextCompat.getDrawable(TMEBackgroundContext.getContext(), R.drawable.shape_ring);

        imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.shape_ring));
        imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.shape_ring));
        imageView.setImageDrawable(ContextCompat.getDrawable(TMEBackgroundContext.getContext(), R.drawable.shape_ring));
    }

    //不支持的：
    /**  java.lang.ArrayIndexOutOfBoundsException: 0
     *   holder?.avatarView?.setImageDrawable(ContextCompat.getDrawable(Global.getContext(), R.drawable.party_pictureframe_empty))
     *   holder.avatarView?.setImageDrawable(Global.getResources().getDrawable(R.drawable.party_emptystate_participants))
     *   context.getDrawable(R.drawable.shape_ring)
     */

}
