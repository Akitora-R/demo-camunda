package me.aki.demo.camunda.delegate.impl;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.delegate.TaskDelegate;
import me.aki.demo.camunda.enums.TaskDelegateType;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageTaskDelegate implements TaskDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        log.info("执行消息委派");
    }

    @Override
    public TaskDelegateType getType() {
        return TaskDelegateType.MESSAGE;
    }
}
