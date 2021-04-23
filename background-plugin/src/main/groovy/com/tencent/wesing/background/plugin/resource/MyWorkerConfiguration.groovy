package com.tencent.wesing.background.plugin.resource

import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.api.Action
import org.gradle.process.JavaForkOptions
import org.gradle.workers.ForkMode
import org.gradle.workers.IsolationMode
import org.gradle.workers.WorkerConfiguration

/**
 * create by zlonghuang on 2021/4/21
 **/

/**
 * 主要解析params参数，文件在这里面
 */
class MyWorkerConfiguration implements WorkerConfiguration {

    private final List<Object> params = new ArrayList<>()

    static final String TAG = "MyWorkerConfiguration"

    @Override
    IsolationMode getIsolationMode() {
        return null
    }

    @Override
    void setIsolationMode(IsolationMode isolationMode) {

    }

    @Override
    void classpath(Iterable<File> iterable) {

    }

    @Override
    void setClasspath(Iterable<File> iterable) {

    }

    @Override
    Iterable<File> getClasspath() {
        return null
    }

    @Override
    ForkMode getForkMode() {
        return null
    }

    @Override
    void setForkMode(ForkMode forkMode) {

    }

    @Override
    void setDisplayName(String s) {

    }

    @Override
    void params(Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            params.add(objects[i])
        }
    }

    @Override
    void setParams(Object... objects) {
        this.params.clear()
        Collections.addAll(this.params, params);
    }

    @Override
    Object[] getParams() {
        return this.params.toArray()
    }

    @Override
    String getDisplayName() {
        return null
    }

    @Override
    void forkOptions(Action<? super JavaForkOptions> action) {

    }

    @Override
    JavaForkOptions getForkOptions() {
        return null
    }

    List<Object> getParamsList() {
        return params
    }
}