package me.aki.demo.camunda.entity;

import lombok.Getter;

@Getter
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    private R() {
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 0;
        r.data = data;
        return r;
    }
}
