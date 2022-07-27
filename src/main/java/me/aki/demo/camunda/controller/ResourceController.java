package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.provider.FormDataProvider;
import me.aki.demo.camunda.provider.SysUserProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resource")
@Slf4j
public class ResourceController {
    private final ApplicationContext applicationContext;

    public ResourceController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {

    }

    @GetMapping("/provider/user")
    public R<List<String>> getUserProviderNames() {
        return R.ok(Arrays.asList(applicationContext.getBeanNamesForType(SysUserProvider.class)));
    }

    @GetMapping("/provider/form")
    public R<List<String>> getFormProviderNames() {
        return R.ok(Arrays.asList(applicationContext.getBeanNamesForType(FormDataProvider.class)));
    }
}
