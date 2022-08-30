package me.aki.demo.camunda.provider;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.aki.demo.camunda.enums.SourceBizType;

public interface BizDataProvider<T> {
    IPage<T> getData(String identifier);

    SourceBizType getType();
}
