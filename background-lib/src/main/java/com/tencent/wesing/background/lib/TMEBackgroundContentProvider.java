package com.tencent.wesing.background.lib;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.wesing.background.lib.bean.TMEBackgroundMap;

public class TMEBackgroundContentProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        if(getContext() != null && getContext() instanceof Application){
            TMEBackgroundLibrary.inject(getContext());
            TMEBackgroundContext.setApplicationContext(getContext());
            TMEBackgroundMap.startParseAttribute();
            ((Application) getContext()).registerActivityLifecycleCallbacks(new TMEActivityLifecycleRegister());
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
