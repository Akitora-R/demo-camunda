package me.aki.demo.camunda.provider.impl;

import me.aki.demo.camunda.enums.FormItemType;
import me.aki.demo.camunda.provider.FormDataProvider;
import me.aki.demo.camunda.util.TreeUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component("GEO_DATA")
public class FormGEODataProvider implements FormDataProvider<List<TreeUtils.TreeNode<Map<String, Object>>>> {
    @Override
    public List<TreeUtils.TreeNode<Map<String, Object>>> getData() {
        return Collections.emptyList();
    }

    @Override
    public FormItemType getType() {
        return FormItemType.CASCADING;
    }
}
