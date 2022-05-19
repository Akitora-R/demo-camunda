package me.aki.demo.camunda.config;

import me.aki.demo.camunda.entity.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class RestAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public R<?> handleException(Exception e) {
        e.printStackTrace();
        return R.fail(e.getMessage());
    }
}
