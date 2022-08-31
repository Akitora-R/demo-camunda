package me.aki.demo.camunda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.enums.SourceBizType;
import me.aki.demo.camunda.provider.DataProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/resource")
@Slf4j
public class ResourceController {

    private final DataProvider dataProvider;

    public ResourceController(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @GetMapping("/user/names")
    public R<Collection<String>> getUserProviderNames() {
        return R.ok(dataProvider.getUserDataProviderMap().keySet());
    }

    @GetMapping("/form/names")
    public R<Collection<String>> getFormProviderNames() {
        return R.ok(dataProvider.getFormDataProviderMap().keySet());
    }

    @GetMapping("/biz/names")
    public R<Collection<SourceBizType>> getBizProviderNames() {
        return R.ok(dataProvider.getBizDataProviderMap().keySet());
    }

    @GetMapping("/form/data/{name}")
    public ResponseEntity<R<?>> getFormDataByName(@PathVariable("name") String name) {
        var p = dataProvider.getFormDataProviderMap().get(name);
        if (p != null) {
            return ResponseEntity.ok(R.ok(p.getData()));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail());
    }

    @GetMapping("/user/data/{name}")
    public ResponseEntity<R<?>> getUserDataByName(@PathVariable("name") String name) {
        var p = dataProvider.getUserDataProviderMap().get(name);
        if (p != null) {
            return ResponseEntity.ok(R.ok(p.getUser("self_user_id")));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail());
    }

    @GetMapping("/biz/data/{type}/{id}")
    public ResponseEntity<R<?>> getBizDataDetail(@PathVariable("type") SourceBizType type, @PathVariable("id") String id) {
        var p = dataProvider.getBizDataProviderMap().get(type);
        if (p != null) {
            return ResponseEntity.ok(R.ok(p.getData(id)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail());
    }
}
