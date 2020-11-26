package com.xiaoju.uemc.turbo.engine.dto;

import com.xiaoju.uemc.turbo.engine.model.InstanceData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 项目名称：turbo
 * 类 名 称：InstanceDataResult
 * 类 描 述：
 * 创建时间：2020/11/26 11:07 AM
 * 创 建 人：didiwangxing
 */
@Data
@ToString(callSuper = true)
public class InstanceDataResult extends CommonResult {
    private List<InstanceData> variables;
}
