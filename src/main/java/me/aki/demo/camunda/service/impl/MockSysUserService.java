package me.aki.demo.camunda.service.impl;

import me.aki.demo.camunda.entity.SysUser;
import me.aki.demo.camunda.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class MockSysUserService implements SysUserService {
    @Override
    public SysUser getCurrentUser() {
        SysUser su = new SysUser();
        su.setId("0");
        su.setUsername("mock user");
        return su;
    }
}
