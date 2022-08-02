package me.aki.demo.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/*
spring event bridge 的全局事件监听方式
 */
@Component
@Slf4j
public class SpringBridgeListener {
    @EventListener
    public void onTaskEvent(DelegateTask taskDelegate) {
        log.debug("onTaskEvent1 {}", taskDelegate);
    }

    @EventListener
    public void onTaskEvent(TaskEvent taskEvent) {
        log.debug("onTaskEvent2 {}", taskEvent);
    }

    @EventListener
    public void onExecutionEvent(DelegateExecution executionDelegate) {
        log.debug("onExecutionEvent1 {}", executionDelegate);
    }

    @EventListener
    public void onExecutionEvent(ExecutionEvent executionEvent) {
        log.debug("onExecutionEvent2 {}", executionEvent);
    }

    @EventListener
    public void onHistoryEvent(HistoryEvent historyEvent) {
        log.debug("onHistoryEvent {}", historyEvent);
    }
}
