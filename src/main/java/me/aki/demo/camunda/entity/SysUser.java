package me.aki.demo.camunda.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模拟用户数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysUser {
    private String id;
    private String userCode;
    private String username;
}
