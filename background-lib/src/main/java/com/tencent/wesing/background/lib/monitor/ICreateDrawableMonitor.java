package com.tencent.wesing.background.lib.monitor;

/**
 * create by zlonghuang on 2021/7/13
 *
 * 监控数据
 **/
public interface ICreateDrawableMonitor {

    /**
     * @param isSystem  是不是使用系统的getDrawable
     * @param costTime  创建drawable耗时
     * @param drawableId  drawableId
     */
    void onGetDrawable(boolean isSystem, long costTime, int drawableId);


    /**
     * 使用fastDrawable失败上报
     * @param drawableId  drawableId
     * @param errorMsg   错误信息
     */
    void onGetDrawableError(int drawableId, String errorMsg);
}
