package me.aki.demo.camunda.entity.dto;

import lombok.Data;
import me.aki.demo.camunda.entity.SysUser;

import java.util.Map;

/**
 * <p>
 * 传入camunda流程实例中的POJO类。
 * </p>
 * <p>
 * 每次流程开始时默认提供发起人账号基本信息作为流程变量传入，
 * 并且可以引入表单定义中的变量，两者都可作为流程中的分支判断参数。
 * </p>
 */
@Data
public class ProcessInstanceVariableDTO {
    private SysUser sysUser;
    private Map<String, Object> variables;
}
