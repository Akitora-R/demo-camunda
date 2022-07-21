package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.service.BpmnService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
@Slf4j
public class ProcController {

    private final BpmnService bpmnService;

    public ProcController(BpmnService bpmnService) {
        this.bpmnService = bpmnService;
    }

    @PostMapping("/definition")
    public R<Object> createDefinition(
            @RequestBody
            @Validated
            ProcDefDTO dto) {
        log.info("{}", dto);
        bpmnService.createBpmnProcess(dto);
        return R.ok();
    }

    @GetMapping("/definition")
    public R<Object> listDefinition() {
        return R.ok();
    }

    @GetMapping("/definition/{id}")
    public R<Object> getDefinitionDetail(@PathVariable String id) {
        return R.ok();
    }

    @DeleteMapping("/definition/{id}")
    public R<Object> delDefinition(@PathVariable String id) {
        return R.ok();
    }

    @PostMapping("/instance")
    public R<Object> createProcessInstance() {
        return R.ok();
    }

    @GetMapping("/instance")
    public R<Object> listProcessInstance() {
        return R.ok();
    }

    @GetMapping("/instance/{businessKey}")
    public R<Object> getProcessInstanceDetail(@PathVariable String businessKey) {
        return R.ok();
    }

    @DeleteMapping("/instance/{businessKey}")
    public R<Object> delProcessInstance(@PathVariable String businessKey) {
        return R.ok();
    }
}
