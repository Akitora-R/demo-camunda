package me.aki.demo.camunda.provider;

import me.aki.demo.camunda.entity.SysUser;

public interface SysUserProvider {
    SysUser getUser(String userId);
}
