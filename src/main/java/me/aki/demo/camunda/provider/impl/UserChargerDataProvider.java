package me.aki.demo.camunda.provider.impl;

import me.aki.demo.camunda.entity.SysUser;
import me.aki.demo.camunda.provider.UserDataProvider;
import me.aki.demo.camunda.service.SysUserService;
import org.springframework.stereotype.Component;

@Component("CHARGER_USER")
public class UserChargerDataProvider implements UserDataProvider {
    private final SysUserService sysUserService;

    public UserChargerDataProvider(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public SysUser getUser(String userId) {
        SysUser su = new SysUser();
        su.setId("2");
        su.setUserCode("charger_code");
        su.setUsername("某主管");
        return su;
    }
}
