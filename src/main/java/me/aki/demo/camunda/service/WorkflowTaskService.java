package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.ProcDefNode;
import me.aki.demo.camunda.entity.ProcInstTaskProp;
import me.aki.demo.camunda.entity.dto.TaskDTO;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class WorkflowTaskService {
    private final TaskService taskService;
    private final ProcDefService procDefService;
    private final ProcInstTaskPropService procInstTaskPropService;
    private final ProcDefNodeService procDefNodeService;
    private final static String FROM_APPROVAL_KEY = "approval";
    private final static String FROM_COMMENT_KEY = "comment";

    public WorkflowTaskService(TaskService taskService, ProcDefService procDefService, ProcInstTaskPropService procInstTaskPropService, ProcDefNodeService procDefNodeService) {
        this.taskService = taskService;
        this.procDefService = procDefService;
        this.procInstTaskPropService = procInstTaskPropService;
        this.procDefNodeService = procDefNodeService;
    }

    public void completeTask(TaskDTO dto) {
        Task task = taskService.createTaskQuery().taskId(dto.getTaskId()).singleResult();
        Assert.notNull(task, "taskId:{} 有误", dto.getTaskId());
        final String camundaProcInstId = task.getProcessInstanceId();
        final String camundaTaskInstId = task.getId();
        Map<String, String> m = Optional.ofNullable(dto.getForm())
                .map(TaskDTO.TaskFormInstDTO::getItemList).orElse(Collections.emptyList()).stream()
                .collect(Collectors.toMap(TaskDTO.TaskFormInstDTO.TaskFormInstItemDTO::getFormItemKey, TaskDTO.TaskFormInstDTO.TaskFormInstItemDTO::getFormItemVal));
        String approvalStr = m.get(FROM_APPROVAL_KEY);
        Assert.isTrue(Boolean.TRUE.toString().equalsIgnoreCase(approvalStr) || Boolean.FALSE.toString().equals(approvalStr), "approval:{} 参数有误", approvalStr);
        String comment = m.get(FROM_COMMENT_KEY);
        ProcDef procDef = procDefService.lambdaQuery().eq(ProcDef::getCamundaProcDefId, task.getProcessDefinitionId()).one();
        ProcDefNode procDefNode = getByTask(task);
        BiFunction<String, String, ProcInstTaskProp> f = getPropGenFunc(camundaProcInstId, camundaTaskInstId, procDef.getId(), procDefNode.getId());
        Map<String, Object> varMap = new HashMap<>();
        varMap.put(FROM_APPROVAL_KEY, Boolean.parseBoolean(approvalStr));
        procInstTaskPropService.save(f.apply(FROM_APPROVAL_KEY, approvalStr));
        if (StrUtil.isNotBlank(comment)) {
            varMap.put(FROM_COMMENT_KEY, comment);
            procInstTaskPropService.save(f.apply(FROM_COMMENT_KEY, comment));
        }
        taskService.complete(dto.getTaskId(), varMap);
    }

    private ProcDefNode getByTask(Task task) {
        return procDefNodeService.findByCamundaBpmnElemId(task.getTaskDefinitionKey(), task.getProcessDefinitionId());
    }

    private BiFunction<String, String, ProcInstTaskProp> getPropGenFunc(String camundaProcInstId, String camundaTaskInstId, String procDefId, String procDefNodeId) {
        return (key, val) -> {
            ProcInstTaskProp p = new ProcInstTaskProp();
            p.setCamundaProcInstId(camundaProcInstId);
            p.setCamundaTaskInstId(camundaTaskInstId);
            p.setProcDefId(procDefId);
            p.setProcDefNodeId(procDefNodeId);
            p.setPropKey(key);
            p.setPropVal(val);
            return p;
        };
    }
}
