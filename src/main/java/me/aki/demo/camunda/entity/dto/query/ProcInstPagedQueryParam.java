package me.aki.demo.camunda.entity.dto.query;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.lang.Pair;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.aki.demo.camunda.entity.dto.PagedQueryParam;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProcInstPagedQueryParam extends PagedQueryParam<ProcInstPagedQuery> {

    @Override
    public ProcInstPagedQuery getQuery() {
        ProcInstPagedQuery q = new ProcInstPagedQuery();
        if (query != null) {
            List<Pair<String, String>> paramPairs = Arrays.stream(query.split(querySep)).map(this::parseQueryParam).toList();
            Map<String, List<Pair<String, String>>> m = paramPairs.stream().collect(Collectors.groupingBy(Pair::getKey));
            ProcInstPagedQuery.ProcInstStatus status = Opt.ofTry(() -> ProcInstPagedQuery.ProcInstStatus.valueOf(m.get("status").get(0).getValue())).orElse(null);
            q.setStatus(status);
        }
        return q;
    }
}
