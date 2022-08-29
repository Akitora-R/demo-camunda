package me.aki.demo.camunda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.enums.SourceBizType;
import me.aki.demo.camunda.provider.BizFormDataProvider;
import me.aki.demo.camunda.provider.FormDataProvider;
import me.aki.demo.camunda.provider.UserDataProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/resource")
@Slf4j
public class ResourceController {

    // FIXME: 2022/7/28 unify it into one single service
    private final Map<String, FormDataProvider<?>> formDataProviderMap;
    private final Map<String, UserDataProvider> userDataProviderMap;
    private final Map<SourceBizType, BizFormDataProvider<?>> bizFormDataProviderMap;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ResourceController(ApplicationContext applicationContext) {
        this.formDataProviderMap = (Map) applicationContext.getBeansOfType(FormDataProvider.class);
        this.userDataProviderMap = applicationContext.getBeansOfType(UserDataProvider.class);
        this.bizFormDataProviderMap = (Map) applicationContext.getBeansOfType(BizFormDataProvider.class)
                .values().stream().collect(Collectors.groupingBy(BizFormDataProvider::getType));
    }

    @GetMapping("/user/names")
    public R<Collection<String>> getUserProviderNames() {
        return R.ok(userDataProviderMap.keySet());
    }

    @GetMapping("/form/names")
    public R<Collection<String>> getFormProviderNames() {
        return R.ok(formDataProviderMap.keySet());
    }

    @GetMapping("/biz/names")
    public R<Collection<SourceBizType>> getBizFormProviderNames() {
        return R.ok(bizFormDataProviderMap.keySet());
    }

    @GetMapping("/form/data/{name}")
    public ResponseEntity<R<?>> getFormDataByName(@PathVariable("name") String name) {
        var p = formDataProviderMap.get(name);
        if (p != null) {
            return ResponseEntity.ok(R.ok(p.getData()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail());
    }

    @GetMapping("/user/data/{name}")
    public ResponseEntity<R<?>> getUserDataByName(@PathVariable("name") String name) {
        var p = userDataProviderMap.get(name);
        if (p != null) {
            return ResponseEntity.ok(R.ok(p.getUser("self_user_id")));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail());
    }

    @GetMapping("/biz/data/{type}/{id}")
    public ResponseEntity<R<IPage<?>>> getBizDataByType(@PathVariable("type") SourceBizType type, @PathVariable("id") String id) {
        var p = bizFormDataProviderMap.get(type);
        if (p != null) {
            return ResponseEntity.ok(R.ok(p.getData(id)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail());
    }
}
