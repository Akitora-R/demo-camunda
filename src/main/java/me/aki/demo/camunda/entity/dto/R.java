package me.aki.demo.camunda.entity.dto;

import lombok.Getter;

@Getter
public class R<T> {
    private Integer code;
    private String msg;
    private T data;

    private R() {
    }

    public static <T> R<T> ok() {
        R<T> r = new R<>();
        r.code = 0;
        return r;
    }

    public static <T> R<T> ok(T data) {
        R<T> r = new R<>();
        r.code = 0;
        r.data = data;
        return r;
    }

    public static <T> R<T> fail() {
        R<T> r = new R<>();
        r.code = 1;
        return r;
    }
    public static <T> R<T> fail(String msg) {
        R<T> r = new R<>();
        r.code = 1;
        r.msg = msg;
        return r;
    }
}
