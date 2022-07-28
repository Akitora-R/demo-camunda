package me.aki.demo.camunda.service.impl;

import me.aki.demo.camunda.entity.SysUser;
import me.aki.demo.camunda.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class MockSysUserService implements SysUserService {
    @Override
    public SysUser getCurrentUser() {
        return new SysUser("mock_user", "Mock User");
    }
}
