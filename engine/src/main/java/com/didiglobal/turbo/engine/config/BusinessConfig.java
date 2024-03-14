package com.didiglobal.turbo.engine.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: james zhangxiao
 * @Date: 11/30/22
 * @Description: 业务配置
 */
@Component
public class BusinessConfig {

    @Value("${callActivity.nested.level:#{null}}")
    private String callActivityNestedLevel;

    public static final int COMPUTING_FLOW_NESTED_LEVEL = -1; // 计算流程嵌套级别
    public static final int MIN_FLOW_NESTED_LEVEL = 0; // 流程不使用CallActivity节点
    public static final int MAX_FLOW_NESTED_LEVEL = 10;

    /**
     * 根据调用者查询callActivityNestedLevel
     * <p>
     * 例如：如果flowA引用了flowB，则flowA的callActivityNestedLevel等于1。
     * 例如：如果flowA引用了flowB，flowB引用了flowC，则flowA的callActivityNestedLevel等于2。
     *
     * @param caller 调用者
     * @return -1 表示无限制
     */
    public int getCallActivityNestedLevel(String caller) {
        if (StringUtils.isBlank(callActivityNestedLevel)) {
            return MAX_FLOW_NESTED_LEVEL;
        }
        JSONObject callActivityNestedLevelJO = JSON.parseObject(callActivityNestedLevel);
        if (callActivityNestedLevelJO.containsKey(caller)) {
            int callActivityNestedLevel = callActivityNestedLevelJO.getIntValue(caller);
            if (MAX_FLOW_NESTED_LEVEL < callActivityNestedLevel) {
                return MAX_FLOW_NESTED_LEVEL;
            } else {
                return callActivityNestedLevel;
            }
        } else {
            return MAX_FLOW_NESTED_LEVEL;
        }
    }
}
