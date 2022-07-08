package me.aki.demo.camunda;

import me.aki.demo.camunda.entity.FormDef;
import me.aki.demo.camunda.service.FormDefService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FormTests {
    @Autowired
    FormDefService formDefService;

    @Test
    void create() {
        FormDef formDef = new FormDef();
        formDef.setTitle("demo form 1");
        formDefService.save(formDef);
    }
}
