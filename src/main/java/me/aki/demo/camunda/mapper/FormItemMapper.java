package me.aki.demo.camunda.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.aki.demo.camunda.entity.FormItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FormItemMapper extends BaseMapper<FormItem> {
}
