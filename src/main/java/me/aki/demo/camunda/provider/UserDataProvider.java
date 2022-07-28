package me.aki.demo.camunda.provider;

import me.aki.demo.camunda.entity.SysUser;

public interface UserDataProvider {
    SysUser getUser(String userId);
}
