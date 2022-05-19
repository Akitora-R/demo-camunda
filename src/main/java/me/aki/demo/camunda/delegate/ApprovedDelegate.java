package me.aki.demo.camunda.delegate;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApprovedDelegate implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        log.info("执行ApprovedDelegate {}", execution);
    }
}
