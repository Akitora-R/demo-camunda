package me.aki.demo.camunda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.entity.dto.query.ProcInstPagedQueryParam;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import me.aki.demo.camunda.entity.vo.ProcInstVO;
import me.aki.demo.camunda.service.WorkflowProcService;
import me.aki.demo.camunda.service.ProcDefService;
import me.aki.demo.camunda.util.ReqUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
@Slf4j
public class ProcController {

    private final WorkflowProcService workflowProcService;
    private final ProcDefService procDefService;

    public ProcController(WorkflowProcService workflowProcService, ProcDefService procDefService) {
        this.workflowProcService = workflowProcService;
        this.procDefService = procDefService;
    }

    @PostMapping("/definition")
    public R<ProcDefDTO> createDefinition(@RequestBody @Validated ProcDefDTO dto) {
        workflowProcService.createProcessDefinition(dto);
        return R.ok(dto);
    }

    @GetMapping("/definition")
    public R<IPage<ProcDef>> listDefinition(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return R.ok(procDefService.page(page, size));
    }

    @GetMapping("/definition/{id}")
    public ResponseEntity<R<ProcDefVO>> getDefinitionDetail(@PathVariable String id) {
        ProcDefVO vo = workflowProcService.getProcDefVOById(id);
        return ReqUtil.nullTo404(vo);
    }

    @DeleteMapping("/definition/{id}")
    public R<Object> delDefinition(@PathVariable String id) {
        // TODO: 2022/7/28
        return R.ok();
    }

    @PostMapping("/instance")
    public R<Object> createProcessInstance(@RequestBody @Validated ProcInstDTO dto) {
        workflowProcService.createProcessInstance(dto);
        return R.ok();
    }

    @GetMapping("/instance")
    public R<Object> listProcessInstance(ProcInstPagedQueryParam query) {
        return R.ok(workflowProcService.procInstPagedQuery(query));
    }

    @GetMapping("/instance/{id}")
    public ResponseEntity<R<ProcInstVO>> getProcessInstanceDetail(@PathVariable String id) {
        ProcInstVO vo = workflowProcService.procInstDetailById(id);
        return ReqUtil.nullTo404(vo);
    }

    @DeleteMapping("/instance/{id}")
    public R<Object> delProcessInstance(@PathVariable String id) {
        // TODO: 2022/7/28
        return R.ok();
    }

    @PutMapping("/task/{id}")
    public R<Object> completeTask(@PathVariable String id) {
        return R.ok();
    }
}
