package com.didiglobal.turbo.engine.engine;

import com.didiglobal.turbo.engine.bo.NodeInstance;
import com.didiglobal.turbo.engine.entity.FlowDefinitionPO;
import com.didiglobal.turbo.engine.entity.FlowDeploymentPO;
import com.didiglobal.turbo.engine.model.*;
import com.didiglobal.turbo.engine.param.*;
import com.didiglobal.turbo.engine.result.*;

/**
 * Turbo的入口
 * <p>
 * 主要提供以下功能：
 * 1. 描述和部署名为flow的流程；
 * 2. 处理和驱动已部署的流程。
 * <p>
 */
public interface ProcessEngine {

    /**
     * 使用flowKey和描述信息创建一个流程（{@link FlowDefinitionPO}）。
     * 注意：flow的{@link FlowModel}为空。
     *
     * @param createFlowParam flowKey: flow的业务关键字
     *                        flowName/operator/remark: 描述flow
     * @return {@link CreateFlowParam} 主要包括flowModuleId以表示唯一的流程。
     */
    CreateFlowResult createFlow(CreateFlowParam createFlowParam);

    /**
     * 通过flowModuleId更新一个流程。设置/更新flowModel或更新描述信息。
     *
     * @param updateFlowParam flowModuleId: 指定要更新的流程
     *                        flowKey/flowName/flowModel/remark: 要更新的内容
     */
    UpdateFlowResult updateFlow(UpdateFlowParam updateFlowParam);

    /**
     * 通过flowModuleId部署一个流程。
     * <p>
     * 每次创建一个{@link FlowDeploymentPO}。
     * 仅在部署后才能启动流程处理。
     *
     * @param deployFlowParam flowModuleId: 指定要部署的流程
     * @return {@link DeployFlowResult} 主要包含flowDeployId以表示部署的唯一记录。
     */
    DeployFlowResult deployFlow(DeployFlowParam deployFlowParam);

    /**
     * 获取包括flowModel内容、状态和描述信息的流程信息。
     * <p>
     * 当flowDeployId不为空时，将通过flowDeployId查询。否则，将通过flowModuleId查询。
     *
     * @param getFlowModuleParam flowModuleId 指定要获取信息的流程 {@link FlowDefinitionPO}
     *                           flowDeployId 指定要获取信息的流程 {@link FlowDeploymentPO}
     */
    FlowModuleResult getFlowModule(GetFlowModuleParam getFlowModuleParam);

    /**
     * 启动流程
     * <p>
     * 1. 每次根据指定的流程创建一个流程实例({@link com.didiglobal.turbo.engine.entity.FlowInstancePO})；
     * 2. 处理流程实例，从唯一的{@link StartEvent}节点开始，直到达到一个{@link UserTask}节点或{@link EndEvent}节点。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param startProcessParam flowDeployId / flowModuleId: 指定要处理的流程
     *                          variables: 驱动流程所需的输入数据（如果需要）
     * @return {@link StartProcessResult} 主要包含flowInstanceId和activeTaskInstance({@link NodeInstance})，
     * 描述要提交的UserTask或EndEvent节点实例。
     */
    StartProcessResult startProcess(StartProcessParam startProcessParam);

    /**
     * 提交先前创建的流程实例中挂起的UserTask并继续处理。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param commitTaskParam flowInstanceId: 指定任务的流程实例
     *                        nodeInstanceId: 指定要提交的任务
     *                        variables: 驱动流程所需的输入数据（如果需要）
     * @return {@link CommitTaskResult} 类似于 {@link #startProcess(StartProcessParam)}
     */
    CommitTaskResult commitTask(CommitTaskParam commitTaskParam);

    /**
     * 回滚任务
     * <p>
     * 根据历史节点实例列表，将指定的流程实例中挂起的UserTask回滚，直到达到一个UserTask节点或StartEvent节点。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param rollbackTaskParam flowInstanceId / nodeInstanceId 类似于 {@link #commitTask(CommitTaskParam)}
     * @return {@link RollbackTaskResult} 类似于 {@link #commitTask(CommitTaskParam)}
     */
    RollbackTaskResult rollbackTask(RollbackTaskParam rollbackTaskParam);

    /**
     * 终止流程
     * <p>
     * 如果指定的流程实例已完成，则忽略。否则，将流程实例的状态设置为终止。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param flowInstanceId
     * @return {@link TerminateResult} 类似于 {@link #commitTask(CommitTaskParam)}，不包含activeTaskInstance。
     */
    TerminateResult terminateProcess(String flowInstanceId);

    /**
     * 终止流程
     * <p>
     * 如果指定的流程实例已完成，则忽略。否则，将流程实例的状态设置为终止。
     *
     * @param flowInstanceId
     * @param effectiveForSubFlowInstance
     * @return {@link TerminateResult} 类似于 {@link #commitTask(CommitTaskParam)}，不包含activeTaskInstance。
     */
    TerminateResult terminateProcess(String flowInstanceId, boolean effectiveForSubFlowInstance);

    /**
     * 获取指定流程实例的历史UserTask列表
     * <p>
     * 获取指定流程实例的已处理UserTask列表，按处理时间降序排列。
     * 注意：列表中包括活动的UserTask和已完成的UserTask，不包括已禁用的UserTask。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param flowInstanceId
     */
    NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId);

    /**
     * 获取指定流程实例的历史UserTask列表
     * <p>
     * 获取指定流程实例的已处理UserTask列表，按处理时间降序排列。
     * 注意：列表中包括活动的UserTask和已完成的UserTask，不包括已禁用的UserTask。
     *
     * @param flowInstanceId
     * @param effectiveForSubFlowInstance
     */
    NodeInstanceListResult getHistoryUserTaskList(String flowInstanceId, boolean effectiveForSubFlowInstance);

    /**
     * 获取指定流程实例的已处理元素实例列表，主要用于显示快照视图。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param flowInstanceId 流程实例ID
     * @return {@link ElementInstanceListResult} 历史执行的节点列表
     */
    ElementInstanceListResult getHistoryElementList(String flowInstanceId);

    /**
     * 获取指定流程实例的已处理元素实例列表，主要用于显示快照视图。
     *
     * @param flowInstanceId 流程实例ID
     * @param effectiveForSubFlowInstance
     * @return {@link ElementInstanceListResult} 历史执行的节点列表
     */
    ElementInstanceListResult getHistoryElementList(String flowInstanceId, boolean effectiveForSubFlowInstance);

    /**
     * 获取指定流程实例的最新{@link InstanceData}列表。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param flowInstanceId
     */
    InstanceDataListResult getInstanceData(String flowInstanceId);

    /**
     * 获取指定流程实例的最新{@link InstanceData}列表。
     *
     * @param flowInstanceId
     * @param effectiveForSubFlowInstance
     */
    InstanceDataListResult getInstanceData(String flowInstanceId, boolean effectiveForSubFlowInstance);

    /**
     * 获取指定实例数据的{@link InstanceData}列表。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param flowInstanceId
     * @param instanceDataId
     */
    InstanceDataListResult getInstanceData(String flowInstanceId, String instanceDataId);

    /**
     * 获取指定实例数据的{@link InstanceData}列表。
     *
     * @param flowInstanceId
     * @param instanceDataId
     * @param effectiveForSubFlowInstance
     */
    InstanceDataListResult getInstanceData(String flowInstanceId, String instanceDataId, boolean effectiveForSubFlowInstance);

    /**
     * 根据给定的流程实例和节点实例，获取节点实例信息。
     *
     * <p>
     * 默认情况下对SubFlowInstance有效
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     */
    NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId);

    /**
     * 根据给定的流程实例和节点实例，获取节点实例信息。
     *
     * @param flowInstanceId
     * @param nodeInstanceId
     * @param effectiveForSubFlowInstance
     */
    NodeInstanceResult getNodeInstance(String flowInstanceId, String nodeInstanceId, boolean effectiveForSubFlowInstance);

    /**
     * 根据给定的流程实例，获取流程实例信息。
     *
     * @param flowInstanceId
     */
    FlowInstanceResult getFlowInstance(String flowInstanceId);
}
