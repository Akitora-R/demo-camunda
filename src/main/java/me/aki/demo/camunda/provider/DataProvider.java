package me.aki.demo.camunda.provider;

import lombok.Getter;
import me.aki.demo.camunda.enums.SourceBizType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DataProvider {
    @Getter
    private final Map<String, FormDataProvider<?>> formDataProviderMap;
    @Getter
    private final Map<SourceBizType, BizDataProvider<?, ?>> bizDataProviderMap;
    @Getter
    private final Map<String, BeanProvider<?>> beanProviderMap;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public DataProvider(ApplicationContext applicationContext) {
        this.formDataProviderMap = (Map) applicationContext.getBeansOfType(FormDataProvider.class);
        this.bizDataProviderMap = (Map) applicationContext.getBeansOfType(BizDataProvider.class)
                .values().stream().collect(Collectors.groupingBy(BizDataProvider::getType));
        this.beanProviderMap = (Map) applicationContext.getBeansOfType(BeanProvider.class);
    }
}
