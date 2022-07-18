package me.aki.demo.camunda.delegate;

import me.aki.demo.camunda.enums.TaskDelegateType;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public interface TaskDelegate extends JavaDelegate {
    TaskDelegateType getType();
}
