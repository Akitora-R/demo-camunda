package me.aki.demo.camunda.provider.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import me.aki.demo.camunda.enums.SourceBizType;
import me.aki.demo.camunda.provider.BizFormDataProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CommunityFormDataProvider implements BizFormDataProvider<CommunityFormDataProvider.CommunityInfo> {

    @Override
    public IPage<CommunityInfo> getData(String identifier) {
        CommunityInfo communityInfo = new CommunityInfo();
        communityInfo.setId("demo_1");
        Page<CommunityInfo> p = new Page<>();
        p.setTotal(1);
        p.setSize(1);
        p.setPages(1);
        p.setCurrent(1);
        p.setRecords(Collections.singletonList(communityInfo));
        return p;
    }

    @Override
    public SourceBizType getType() {
        return SourceBizType.COMMUNITY;
    }

    /**
     * 示例数据实体类
     */
    @Data
    public static class CommunityInfo {
        private String id;
    }
}
