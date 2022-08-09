package me.aki.demo.camunda.util;

import me.aki.demo.camunda.entity.dto.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ReqUtil {
    public static <T> ResponseEntity<R<T>> nullTo404(T data) {
        if (data == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(R.fail("数据不存在！"));
        }
        return ResponseEntity.ok(R.ok(data));
    }
}
