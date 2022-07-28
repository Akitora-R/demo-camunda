package me.aki.demo.camunda.service;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.SysUser;
import org.springframework.stereotype.Service;

public interface SysUserService {
    SysUser getCurrentUser();
}
