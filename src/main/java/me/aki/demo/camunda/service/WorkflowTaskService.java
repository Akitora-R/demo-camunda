package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import me.aki.demo.camunda.entity.ProcDefNode;
import me.aki.demo.camunda.entity.ProcDefVariable;
import me.aki.demo.camunda.entity.VariableInst;
import me.aki.demo.camunda.entity.dto.TaskDTO;
import me.aki.demo.camunda.entity.dto.VariableInstDTO;
import me.aki.demo.camunda.enums.VariableSourceType;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkflowTaskService {
    private final TaskService taskService;
    private final ProcDefNodeService procDefNodeService;
    private final ProcDefVariableService procDefVariableService;
    private final VariableInstService variableInstService;

    public WorkflowTaskService(TaskService taskService,
                               ProcDefNodeService procDefNodeService,
                               ProcDefVariableService procDefVariableService,
                               VariableInstService variableInstService) {
        this.taskService = taskService;
        this.procDefNodeService = procDefNodeService;
        this.procDefVariableService = procDefVariableService;
        this.variableInstService = variableInstService;
    }

    public void completeTask(TaskDTO dto) {
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        Assert.notNull(task, "taskId:{} 有误", dto.getTaskId());
        ProcDefNode procDefNode = getByTask(task);
        final String camundaProcInstId = task.getProcessInstanceId();
        Map<String, String> formMap = Optional.ofNullable(dto.getVariableList()).orElse(Collections.emptyList()).stream()
                .filter(e -> e.getSourceType() == VariableSourceType.TASK_FORM).collect(Collectors.toMap(VariableInstDTO::getSourceIdentifier, VariableInstDTO::getVal));
        List<ProcDefVariable> requiredVar = procDefVariableService.lambdaQuery().eq(ProcDefVariable::getProcDefNodeId, procDefNode.getId()).list();
        // 筛选出任务表单变量并从提交的表单中获取
        Map<String, Object> varMap = requiredVar.stream()
                .filter(v -> v.getSourceType() == VariableSourceType.TASK_FORM)
                .peek(v -> {
                    String varVal = formMap.get(v.getSourceIdentifier());
                    Assert.isTrue(varVal != null || !v.getRequired(), "必填变量不存在: {} {}", v.getSourceType(), v.getSourceIdentifier());
                    if (varVal != null) {
                        VariableInst variableInst = new VariableInst();
                        variableInst.setCamundaProcInstId(camundaProcInstId);
                        variableInst.setVariableDefId(v.getId());
                        variableInst.setVariableVal(varVal);
                        variableInstService.save(variableInst);
                    }
                }).collect(Collectors.toMap(ProcDefVariable::getVariableKey, v -> formMap.get(v.getSourceIdentifier()), (a, b) -> a));
        taskService.complete(dto.getTaskId(), varMap);
    }

    private ProcDefNode getByTask(Task task) {
        return procDefNodeService.findByCamundaBpmnElemId(task.getTaskDefinitionKey(), task.getProcessDefinitionId());
    }
}
