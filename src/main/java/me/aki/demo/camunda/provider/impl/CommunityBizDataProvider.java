package me.aki.demo.camunda.provider.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import me.aki.demo.camunda.enums.SourceBizType;
import me.aki.demo.camunda.provider.BizDataProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class CommunityBizDataProvider implements BizDataProvider<CommunityBizDataProvider.CommunityInfo, CommunityBizDataProvider.CommunityQuery> {

    @Override
    public CommunityInfo getData(String identifier) {
        CommunityInfo communityInfo = new CommunityInfo();
        communityInfo.setId("demo_1");
        return communityInfo;
    }

    @Override
    public IPage<CommunityInfo> getPage(CommunityQuery query) {
        return null;
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

    public static class CommunityQuery implements BizDataQuery<CommunityInfo> {
        @Override
        public IPage<CommunityInfo> getPage() {
            return null;
        }

        @Override
        public List<BizDataQueryFormItem> getFormItemList() {
            return null;
        }
    }
}
