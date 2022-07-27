package me.aki.demo.camunda.provider;

import me.aki.demo.camunda.enums.FormItemType;

public interface FormDataProvider<T> {
    T getData();

    FormItemType getType();
}
