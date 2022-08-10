package me.aki.demo.camunda.service;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import me.aki.demo.camunda.entity.dto.TaskDTO;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkflowTaskService {
    private final TaskService taskService;
    private final static String FROM_APPROVAL_KEY = "approval";
    private final static String FROM_COMMENT_KEY = "comment";

    public WorkflowTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void completeTask(TaskDTO dto) {
        Map<String, String> m = Optional.ofNullable(dto.getForm())
                .map(TaskDTO.TaskFormInstDTO::getItemList).orElse(Collections.emptyList()).stream()
                .collect(Collectors.toMap(TaskDTO.TaskFormInstDTO.TaskFormInstItemDTO::getFormItemKey, TaskDTO.TaskFormInstDTO.TaskFormInstItemDTO::getFormItemVal));
        String approvalStr = m.get(FROM_APPROVAL_KEY);
        Assert.isTrue("true".equalsIgnoreCase(approvalStr) || "false".equals(approvalStr), "approval 参数有误");
        String comment = m.get(FROM_COMMENT_KEY);
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("approval", Boolean.parseBoolean(approvalStr));
        if (StrUtil.isNotBlank(comment)) {
            varMap.put("comment", comment);
        }
        // TODO: 2022/8/10 外部表存储变量
        taskService.complete(dto.getTaskId(), varMap);
    }
}
