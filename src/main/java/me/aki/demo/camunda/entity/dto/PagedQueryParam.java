package me.aki.demo.camunda.entity.dto;

import cn.hutool.core.lang.Pair;
import lombok.Setter;

public abstract class PagedQueryParam<T> {
    @Setter
    private Integer page;
    @Setter
    private Integer size;
    protected String query;
    protected static final String querySep = ",";
    protected static final String queryMd = "=";

    public abstract T getQuery();

    protected Pair<String, String> parseQueryParam(String q) {
        String[] split = q.split(queryMd, 2);
        if (split.length < 2) {
            return new Pair<>(split[0], null);
        }
        return new Pair<>(split[0], split[1]);
    }

    public Integer getPage() {
        return page == null ? 1 : page;
    }

    public Integer getSize() {
        return size == null ? 25 : size;
    }
}
