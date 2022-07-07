package me.aki.demo.camunda.controller;

import me.aki.demo.camunda.entity.dto.FormDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.service.FormDefService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form")
public class Form {
    private final FormDefService formDefService;

    public Form(FormDefService formDefService) {
        this.formDefService = formDefService;
    }

    @PostMapping("/")
    public R<FormDTO> create(@RequestBody FormDTO dto) {
        formDefService.saveDTO(dto);
        return R.ok(dto);
    }
}
