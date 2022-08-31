package me.aki.demo.camunda.provider;

import com.baomidou.mybatisplus.core.metadata.IPage;
import me.aki.demo.camunda.enums.FormItemType;
import me.aki.demo.camunda.enums.SourceBizType;

import java.util.List;

public interface BizDataProvider<T, Q extends BizDataProvider.BizDataQuery<T>> {
    T getData(String identifier);

    IPage<T> getPage(Q query);

    SourceBizType getType();

    interface BizDataQuery<T> {
        IPage<T> getPage();

        List<BizDataQueryFormItem> getFormItemList();

        interface BizDataQueryFormItem {
            FormItemType getType();

            String getLabel();

            List<BizDataQueryFormItemProp> getPropList();

            interface BizDataQueryFormItemProp {
                String getPropKey();

                String getPropVal();
            }
        }
    }
}
