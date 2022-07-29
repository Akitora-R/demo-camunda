package me.aki.demo.camunda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import me.aki.demo.camunda.service.BpmnService;
import me.aki.demo.camunda.service.ProcDefService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
@Slf4j
public class ProcController {

    private final BpmnService bpmnService;
    private final ProcDefService procDefService;

    public ProcController(BpmnService bpmnService, ProcDefService procDefService) {
        this.bpmnService = bpmnService;
        this.procDefService = procDefService;
    }

    @PostMapping("/definition")
    public R<ProcDefDTO> createDefinition(
            @RequestBody
            @Validated
            ProcDefDTO dto) {
        bpmnService.createProcessDefinition(dto);
        return R.ok(dto);
    }

    @GetMapping("/definition")
    public R<IPage<ProcDef>> listDefinition(@RequestParam(defaultValue = "1") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size) {
        return R.ok(procDefService.page(page, size));
    }

    @GetMapping("/definition/{id}")
    public R<ProcDefVO> getDefinitionDetail(@PathVariable String id) {
        return R.ok(bpmnService.getProcDefVOById(id));
    }

    @DeleteMapping("/definition/{id}")
    public R<Object> delDefinition(@PathVariable String id) {
        // TODO: 2022/7/28
        return R.ok();
    }

    @PostMapping("/instance")
    public R<Object> createProcessInstance(@RequestBody @Validated ProcInstDTO dto) {
        bpmnService.createProcessInstance(dto);
        return R.ok();
    }

    @GetMapping("/instance")
    public R<Object> listProcessInstance() {
        // TODO: 2022/7/28
        return R.ok();
    }

    @GetMapping("/instance/{businessKey}")
    public R<Object> getProcessInstanceDetail(@PathVariable String businessKey) {
        // TODO: 2022/7/28
        return R.ok();
    }

    @DeleteMapping("/instance/{businessKey}")
    public R<Object> delProcessInstance(@PathVariable String businessKey) {
        // TODO: 2022/7/28
        return R.ok();
    }
}
