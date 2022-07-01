package me.aki.demo.camunda.entity;

import lombok.Getter;

@Getter
public class Tuple<T, U, V> {
    private T val1;
    private U val2;
    private V val3;

    private Tuple() {
    }

    public static <T, U, V> Tuple<T, U, V> of(T val1, U val2, V val3) {
        Tuple<T, U, V> t = new Tuple<>();
        t.val1 = val1;
        t.val2 = val2;
        t.val3 = val3;
        return t;
    }
}
