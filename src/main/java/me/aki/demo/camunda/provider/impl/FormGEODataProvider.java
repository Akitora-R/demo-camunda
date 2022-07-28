package me.aki.demo.camunda.provider.impl;

import me.aki.demo.camunda.enums.FormItemType;
import me.aki.demo.camunda.provider.FormDataProvider;
import me.aki.demo.camunda.util.TreeUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("GEO_DATA")
public class FormGEODataProvider implements FormDataProvider<List<TreeUtils.TreeNode<Map<String, Object>>>> {
    @Override
    public List<TreeUtils.TreeNode<Map<String, Object>>> getData() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "root");
        var root = new TreeUtils.TreeNode<>("1", "0", data,
                Arrays.asList(
                        new TreeUtils.TreeNode<>("2", "1", null, null),
                        new TreeUtils.TreeNode<>("3", "1", null, null)
                ));
        return Collections.singletonList(root);
    }

    @Override
    public FormItemType getType() {
        return FormItemType.CASCADING;
    }
}
