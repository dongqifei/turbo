package com.didiglobal.turbo.engine.util;

import com.didiglobal.turbo.engine.common.ErrorEnum;
import com.didiglobal.turbo.engine.exception.ProcessException;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import groovy.lang.Script;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyUtil {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GroovyUtil.class);

    // 用于缓存Groovy脚本的类
    private static final Map<String, Class> SCRIPT_CLASS_CACHE = new ConcurrentHashMap<String, Class>();

    // 私有构造方法，防止实例化
    private GroovyUtil() {
    }

    /**
     * 执行Groovy表达式并返回结果
     *
     * @param expression Groovy表达式
     * @param dataMap    数据映射
     * @return 执行结果
     * @throws Exception 执行过程中的异常
     */
    public static Object execute(String expression, Map<String, Object> dataMap) throws Exception {
        if (StringUtils.isBlank(expression)) {
            LOGGER.warn("calculate: expression is empty");
            return null;
        }
        try {
            Binding binding = createBinding(dataMap);
            Script shell = createScript(expression, binding);
            Object resultObject = shell.run();
            LOGGER.info("calculate.||expression={}||resultObject={}", expression, resultObject);
            return resultObject;
        } catch (MissingPropertyException mpe) {
            LOGGER.warn("calculate MissingPropertyException.||expression={}||dataMap={}", expression, dataMap);
            throw new ProcessException(ErrorEnum.MISSING_DATA.getErrNo(), mpe.getMessage());
        }
    }

    /**
     * 创建Groovy脚本
     *
     * @param groovyExpression Groovy表达式
     * @param binding          绑定对象
     * @return Groovy脚本
     */
    private static Script createScript(String groovyExpression, Binding binding) {
        Script script;
        if (SCRIPT_CLASS_CACHE.containsKey(groovyExpression)) {
            Class scriptClass = SCRIPT_CLASS_CACHE.get(groovyExpression);
            script = InvokerHelper.createScript(scriptClass, binding);
        } else {
            script = new GroovyShell(binding).parse(groovyExpression);
            SCRIPT_CLASS_CACHE.put(groovyExpression, script.getClass());
        }
        return script;
    }

    /**
     * 创建绑定对象
     *
     * @param infos 数据映射
     * @return 绑定对象
     */
    private static Binding createBinding(Map<String, Object> infos) {
        Binding binding = new Binding();
        if (!infos.isEmpty()) {
            for (Map.Entry<String, Object> entry : infos.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
        }
        return binding;
    }
}
