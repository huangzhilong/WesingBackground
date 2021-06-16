package com.tencent.wesing.background.plugin.ams

import javassist.ClassPath
import javassist.ClassPool
import javassist.CtClass
import org.gradle.api.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by huangzhilong on 19/7/18.
 *
 * 统一管理JavaAssist ClassPool
 */

class AsmClassHelper {


    private static class SingleHolder {
        public static AsmClassHelper instance = new AsmClassHelper()
    }

    static AsmClassHelper getInstance() {
        return SingleHolder.instance
    }

    private AsmClassHelper() {
    }

    private ClassPool mClassPool
    private Map<String, ClassPath> mPathMap

    /**
     * 初始化classPool
     * @param project
     */
    void initPool(Project project) {
        mClassPool = new ClassPool(false)
        mPathMap = new ConcurrentHashMap<>()
        //添加系统类
        ClassPath sysPath = mClassPool.appendSystemPath()
        mPathMap.put('system', sysPath)
        //添加android对应类，这样才能扫到android包底下的类
        ClassPath androidPath = mClassPool.appendClassPath(project.android.bootClasspath[0].toString())
        mPathMap.put('android', androidPath)
    }

    void addClassPath(String path) {
        if (mClassPool == null) {
            return
        }
        if (mPathMap.containsKey(path)) {
            return
        }
        ClassPath classPath = mClassPool.appendClassPath(path)
        mPathMap.put(path, classPath)
    }


    /**
     * 是否是子类
     * @param child
     * @param parent
     * @return
     */
    boolean isSubClass(CtClass child, String parent) {
        if (child.name == parent) {
            return false
        }
        while (child != null) {
            if (child.name == parent) {
                return true
            }
            child = child.getSuperclass()
        }
        return false
    }

    /**
     * 是否资源
     */
    void releaseClassPool() {
        if (mClassPool == null) {
            return
        }
        if (mPathMap.size() > 0) {
            Iterator<String> iterator = mPathMap.keySet().iterator()
            while (iterator.hasNext()) {
                String key = iterator.next()
                ClassPath path = mPathMap.get(key)
                mClassPool.removeClassPath(path)
            }
            mPathMap.clear()
        }
        mClassPool.clearImportedPackages()
    }
}