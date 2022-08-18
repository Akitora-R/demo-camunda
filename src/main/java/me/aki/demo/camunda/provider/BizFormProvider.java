package me.aki.demo.camunda.provider;

import com.baomidou.mybatisplus.core.metadata.IPage;

public interface BizFormProvider<T> {
    IPage<T> getData(String identifier);
}
