package com.didiglobal.turbo.engine.spi;

import com.didiglobal.turbo.engine.model.InstanceData;

import java.util.List;

/**
 * <p> 钩子服务 </p>
 *
 * @author lijinghao
 * @version v1.0
 * @date 2023/2/16 6:59 PM
 */
public interface HookService {

    /**
     * 调用钩子服务，用于在网关节点上执行数据刷新操作。
     * 用法：实现接口并将实例交给Spring进行管理
     *
     * @param flowInstanceId 运行时流程实例ID
     * @param nodeInstanceId 运行中节点实例ID
     * @param nodeKey        运行中节点的关键字
     * @param hookInfoParam 一些信息，您可以刷新
     * @return 新的信息
     */
    List<InstanceData> invoke(String flowInstanceId, String nodeInstanceId, String nodeKey, String hookInfoParam);
}
