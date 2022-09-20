package me.aki.demo.camunda.provider.impl;

import me.aki.demo.camunda.constant.BeanProviderId;
import me.aki.demo.camunda.entity.SysUser;
import me.aki.demo.camunda.entity.dto.VariableDefDTO;
import me.aki.demo.camunda.provider.BeanProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(BeanProviderId.USER_PROVIDER)
public class UserProvider implements BeanProvider<SysUser> {

    @Override
    public SysUser getData(List<VariableDefDTO.VariableDefPropDTO> prop) {
        return new SysUser();
    }
}
