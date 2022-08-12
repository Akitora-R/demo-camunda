package me.aki.demo.camunda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aki.demo.camunda.entity.ProcDefNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcDefNodeMapper extends BaseMapper<ProcDefNode> {
    ProcDefNode selectByCamundaBpmnElemId(@Param("camundaElementId") String camundaElementId,
                                          @Param("camundaProcDefId") String camundaProcDefId);
}
