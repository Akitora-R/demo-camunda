package me.aki.demo.camunda.listener;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.service.SomeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

/*
配置 ExecutionListener 的事件监听方式，需要在特定流程中定义
 */
@Slf4j
@Component
public class DemoExecutionListener implements ExecutionListener {
    private final SomeService someService;

    public DemoExecutionListener(SomeService someService) {
        this.someService = someService;
    }

    @Override
    public void notify(DelegateExecution execution) throws Exception {
        log.info("executionListener 唤起");
        someService.sayHi();
    }
}
