package com.tencent.wesing.background.lib.res;

import android.content.res.AssetFileDescriptor;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.RequiresApi;

import com.tencent.wesing.background.lib.drawable.TMEBackgroundDrawableFactory;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * create by zlonghuang on 2021/6/19
 *
 **/

public class TMEBackgroundResources extends Resources {

    private final static String TAG = "TMEBackgroundResources";

    private Resources mSystemResources;

    TMEBackgroundResources(Resources resources) {
        super(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
        mSystemResources = resources;
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        Log.i(TAG, "TMEBackgroundDrawableFactory createDrawableById : " + id);
        return TMEBackgroundDrawableFactory.createDrawableById(id);
    }

    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return mSystemResources.getText(id);
    }

    @Override
    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        return mSystemResources.getQuantityText(id, quantity);
    }

    @Override
    public String getString(int id) throws NotFoundException {
        return mSystemResources.getString(id);
    }

    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return mSystemResources.getString(id, formatArgs);
    }

    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs)
            throws NotFoundException {
        return mSystemResources.getQuantityString(id, quantity, formatArgs);
    }

    @Override
    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return mSystemResources.getQuantityString(id, quantity);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        return mSystemResources.getText(id, def);
    }

    @Override
    public CharSequence[] getTextArray(int id) throws NotFoundException {
        return mSystemResources.getTextArray(id);
    }

    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        return mSystemResources.getStringArray(id);
    }

    @Override
    public int[] getIntArray(int id) throws NotFoundException {
        return mSystemResources.getIntArray(id);
    }

    @Override
    public TypedArray obtainTypedArray(int id) throws NotFoundException {
        return mSystemResources.obtainTypedArray(id);
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        return mSystemResources.getDimension(id);
    }

    @Override
    public int getDimensionPixelOffset(int id) throws NotFoundException {
        return mSystemResources.getDimensionPixelOffset(id);
    }

    @Override
    public int getDimensionPixelSize(int id) throws NotFoundException {
        return mSystemResources.getDimensionPixelSize(id);
    }

    @Override
    public float getFraction(int id, int base, int pbase) {
        return mSystemResources.getFraction(id, base, pbase);
    }

    @RequiresApi(21)
    @Override
    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
        return mSystemResources.getDrawable(id, theme);
    }

    @SuppressWarnings("deprecation")
    @RequiresApi(15)
    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        return mSystemResources.getDrawableForDensity(id, density);
    }

    @RequiresApi(21)
    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        return mSystemResources.getDrawableForDensity(id, density, theme);
    }

    @SuppressWarnings("deprecation")
    @Override
    public android.graphics.Movie getMovie(int id) throws NotFoundException {
        return mSystemResources.getMovie(id);
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getColor(int id) throws NotFoundException {
        return mSystemResources.getColor(id);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
        return mSystemResources.getColorStateList(id);
    }

    @Override
    public boolean getBoolean(int id) throws NotFoundException {
        return mSystemResources.getBoolean(id);
    }

    @Override
    public int getInteger(int id) throws NotFoundException {
        return mSystemResources.getInteger(id);
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return mSystemResources.getLayout(id);
    }

    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        return mSystemResources.getAnimation(id);
    }

    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        return mSystemResources.getXml(id);
    }

    @Override
    public InputStream openRawResource(int id) throws NotFoundException {
        return mSystemResources.openRawResource(id);
    }

    @Override
    public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
        return mSystemResources.openRawResource(id, value);
    }

    @Override
    public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
        return mSystemResources.openRawResourceFd(id);
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs)
            throws NotFoundException {
        mSystemResources.getValue(id, outValue, resolveRefs);
    }

    @RequiresApi(15)
    @Override
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs)
            throws NotFoundException {
        mSystemResources.getValueForDensity(id, density, outValue, resolveRefs);
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs)
            throws NotFoundException {
        mSystemResources.getValue(name, outValue, resolveRefs);
    }

    @Override
    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        return mSystemResources.obtainAttributes(set, attrs);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        super.updateConfiguration(config, metrics);
        if (mSystemResources != null) { // called from super's constructor. So, need to check.
            mSystemResources.updateConfiguration(config, metrics);
        }
    }

    @Override
    public DisplayMetrics getDisplayMetrics() {
        return mSystemResources.getDisplayMetrics();
    }

    @Override
    public Configuration getConfiguration() {
        return mSystemResources.getConfiguration();
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        return mSystemResources.getIdentifier(name, defType, defPackage);
    }

    @Override
    public String getResourceName(int resid) throws NotFoundException {
        return mSystemResources.getResourceName(resid);
    }

    @Override
    public String getResourcePackageName(int resid) throws NotFoundException {
        return mSystemResources.getResourcePackageName(resid);
    }

    @Override
    public String getResourceTypeName(int resid) throws NotFoundException {
        return mSystemResources.getResourceTypeName(resid);
    }

    @Override
    public String getResourceEntryName(int resid) throws NotFoundException {
        return mSystemResources.getResourceEntryName(resid);
    }

    @Override
    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle)
            throws XmlPullParserException, IOException {
        mSystemResources.parseBundleExtras(parser, outBundle);
    }

    @Override
    public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle)
            throws XmlPullParserException {
        mSystemResources.parseBundleExtra(tagName, attrs, outBundle);
    }
}
