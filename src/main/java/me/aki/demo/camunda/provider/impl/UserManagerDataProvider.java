package me.aki.demo.camunda.provider.impl;

import me.aki.demo.camunda.entity.SysUser;
import me.aki.demo.camunda.provider.UserDataProvider;
import me.aki.demo.camunda.service.SysUserService;
import org.springframework.stereotype.Component;

@Component("MANAGER_USER")
public class UserManagerDataProvider implements UserDataProvider {
    private final SysUserService sysUserService;

    public UserManagerDataProvider(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public SysUser getUser(String userId) {
        SysUser su = new SysUser();
        su.setId("1");
        su.setUserCode("manager_code");
        su.setUsername("某经理");
        return su;
    }
}
