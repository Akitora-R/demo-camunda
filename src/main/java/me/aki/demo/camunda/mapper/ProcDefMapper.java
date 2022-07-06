package me.aki.demo.camunda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aki.demo.camunda.entity.ProcDef;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProcDefMapper extends BaseMapper<ProcDef> {
}
