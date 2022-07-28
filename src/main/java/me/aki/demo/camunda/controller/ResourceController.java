package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.provider.FormDataProvider;
import me.aki.demo.camunda.provider.UserDataProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    // FIXME: 2022/7/28 unify it into one single service
    private final Map<String, FormDataProvider<?>> formDataProviderMap;
    private final Map<String, UserDataProvider> userDataProviderMap;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ResourceController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.formDataProviderMap = (Map) applicationContext.getBeansOfType(FormDataProvider.class);
        this.userDataProviderMap = applicationContext.getBeansOfType(UserDataProvider.class);
    }

    @GetMapping("/user/names")
    public R<List<String>> getUserProviderNames() {
        return R.ok(Arrays.asList(applicationContext.getBeanNamesForType(UserDataProvider.class)));
    }

    @GetMapping("/form/names")
    public R<List<String>> getFormProviderNames() {
        return R.ok(Arrays.asList(applicationContext.getBeanNamesForType(FormDataProvider.class)));
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
}
