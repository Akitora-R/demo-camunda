package me.aki.demo.camunda.entity.dto.query;

import cn.hutool.core.lang.Pair;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aki.demo.camunda.entity.dto.PagedQueryParam;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcInstPagedQueryParam extends PagedQueryParam<ProcInstPagedQuery> {

    @Override
    public ProcInstPagedQuery getQuery() {
        if (query == null) {
            return null;
        }
        List<Pair<String, String>> paramPairs = Arrays.stream(query.split(querySep)).map(this::parseQueryParam).toList();

        return null;
    }
}
