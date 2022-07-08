package me.aki.demo.camunda.config;

import lombok.extern.slf4j.Slf4j;
import me.aki.demo.camunda.entity.dto.R;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class RestAdvice {
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleBindingException(BindException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleException(Exception e) {
        log.error("", e);
        return R.fail(e.getMessage());
    }
}
