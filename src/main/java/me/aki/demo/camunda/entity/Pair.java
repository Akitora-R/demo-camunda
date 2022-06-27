package me.aki.demo.camunda.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<K, V> {
    private K key;
    private V val;

    public Pair() {
    }

    public static <K, V> Pair<K, V> Of(K key, V val) {
        Pair<K, V> p = new Pair<>();
        p.key = key;
        p.val = val;
        return p;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", val=" + val +
                '}';
    }
}
