package me.aki.demo.camunda.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.ProcDef;
import me.aki.demo.camunda.entity.dto.ProcDefDTO;
import me.aki.demo.camunda.entity.dto.ProcInstDTO;
import me.aki.demo.camunda.entity.dto.R;
import me.aki.demo.camunda.entity.dto.TaskDTO;
import me.aki.demo.camunda.entity.dto.converter.NodeConverter;
import me.aki.demo.camunda.entity.dto.node.NodeDTO;
import me.aki.demo.camunda.entity.dto.query.ProcInstPagedQueryParam;
import me.aki.demo.camunda.entity.vo.ProcDefVO;
import me.aki.demo.camunda.entity.vo.ProcInstVO;
import me.aki.demo.camunda.enums.SourceBizType;
import me.aki.demo.camunda.service.ProcDefService;
import me.aki.demo.camunda.service.WorkflowProcService;
import me.aki.demo.camunda.service.WorkflowTaskService;
import me.aki.demo.camunda.util.ReqUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
@Slf4j
public class ProcController {

    private final WorkflowProcService workflowProcService;
    private final WorkflowTaskService workflowTaskService;
    private final ProcDefService procDefService;
    private final NodeConverter nodeConverter;

    public ProcController(WorkflowProcService workflowProcService,
                          WorkflowTaskService workflowTaskService,
                          ProcDefService procDefService,
                          NodeConverter nodeConverter) {
        this.workflowProcService = workflowProcService;
        this.workflowTaskService = workflowTaskService;
        this.procDefService = procDefService;
        this.nodeConverter = nodeConverter;
    }

    @PostMapping("/definition")
    public R<Object> createProcessDefinition(@RequestBody JsonNode body) {
        ProcDefDTO dto = new ProcDefDTO();
        dto.setProcDefName(body.get("procDefName").asText());
        dto.setSourceBizType(SourceBizType.valueOf(body.get("sourceBizType").asText()));
        List<NodeDTO> nodes = nodeConverter.convert(body.get("nodeList"));
        dto.setNodeList(nodes);
        dto.setOriginalJson(body);
        workflowProcService.createProcessDefinition(dto);
        return R.ok();
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

    @PutMapping("/task")
    public R<Object> completeTask(@Validated @RequestBody TaskDTO dto) {
        workflowTaskService.completeTask(dto);
        return R.ok();
    }
}
