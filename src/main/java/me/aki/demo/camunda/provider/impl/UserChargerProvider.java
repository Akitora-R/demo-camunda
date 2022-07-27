package me.aki.demo.camunda.provider.impl;

import me.aki.demo.camunda.entity.SysUser;
import me.aki.demo.camunda.provider.SysUserProvider;
import org.springframework.stereotype.Component;

@Component("CHARGER_USER")
public class UserChargerProvider implements SysUserProvider {
    @Override
    public SysUser getUser(String userId) {
        return null;
    }
}
