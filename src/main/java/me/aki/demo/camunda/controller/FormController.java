package me.aki.demo.camunda.controller;

import me.aki.demo.camunda.entity.dto.FormDefDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.service.FormDefService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/form")
public class FormController {
    private final FormDefService formDefService;

    public FormController(FormDefService formDefService) {
        this.formDefService = formDefService;
    }

    @PostMapping("/create")
    public R<FormDefDTO> create(@RequestBody @Validated FormDefDTO dto) {
        formDefService.saveDTO(dto);
        return R.ok(dto);
    }
}
