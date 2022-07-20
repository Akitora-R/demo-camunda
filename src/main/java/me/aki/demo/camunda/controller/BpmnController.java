package me.aki.demo.camunda.controller;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.service.BpmnService;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bpmn")
@Slf4j
public class BpmnController {

    private final BpmnService bpmnService;

    public BpmnController(BpmnService bpmnService) {
        this.bpmnService = bpmnService;
    }

    @PostMapping("/create")
    public Object create(
            @RequestBody
            @Validated
            ProcDefDTO dto) {
        log.info("{}", dto);
        BpmnModelInstance instance = bpmnService.parse(dto.getProcDefName(), dto.getNodeList());

        return Bpmn.convertToString(instance);
    }
}
