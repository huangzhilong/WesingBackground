package com.tencent.wesing.background.plugin

import com.tencent.wesing.background.plugin.util.BackgroundUtil
import com.tencent.wesing.background.plugin.util.LogUtil
import org.gradle.StartParameter
import org.gradle.TaskExecutionRequest

/**
 * create by zlonghuang on 2021/4/21
 **/

class StartParams {

    private static final TAG = "StartParams"
    private static final String IDEA_SYNC_ACTIVE = "idea.sync.active"
    private static final String BUILD_ASSEMBLE_PREFIX = "assemble"

    private Map<String, String> propertyMap

    private List<String> projectArgs = new LinkedList<>()


    StartParams(StartParameter startParameter) {
        parseStartParameter(startParameter)
        initPropertyMap(startParameter)
    }

    void parseStartParameter(StartParameter startParameter) {
        if (startParameter != null) {
            List<TaskExecutionRequest> requests = startParameter.getTaskRequests();
            for (TaskExecutionRequest request : requests) {
                List<String> args = request.getArgs();
                for (String name : args) {
                    LogUtil.logI(TAG, "parseStartParameter arg: " + name)
                    projectArgs.add(name)
                }
            }
        }
    }

    void initPropertyMap(StartParameter startParameter) {
        if (startParameter != null) {
            propertyMap = startParameter.getSystemPropertiesArgs()
            LogUtil.logI(TAG, "initPropertyMap arg: " + propertyMap)
        }
    }

    boolean isIDESync() {
        String value = propertyMap.get(IDEA_SYNC_ACTIVE);
        return !StringUtils.isEmpty(value) ? Boolean.valueOf(value) : false
    }

    boolean isDebug() {
        if (BackgroundUtil.getCollectSize(projectArgs) <= 0) {
            return false
        }
        for (arg in projectArgs) {
            if (arg.contains(BUILD_ASSEMBLE_PREFIX) && (arg.contains("Debug") || arg.contains("debug"))) {
                return true
            }
        }
        return false
    }
}
