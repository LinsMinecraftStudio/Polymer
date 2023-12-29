package io.github.linsminecraftstudio.polymer.objectutils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TuplePair<A, B> {
    private A a;
    private B b;

    public static <A, B> TuplePair<A, B> of(A a, B b) {
        return new TuplePair<>(a, b);
    }

    public static <A, B> TuplePair<A, B> left(A a) {
        return new TuplePair<>(a, null);
    }

    public static <A, B> TuplePair<A, B> right(B b) {
        return new TuplePair<>(null, b);
    }

    public A getKey() {
        return getA();
    }

    public B getValue() {
        return getB();
    }

    public A getLeft() {
        return getA();
    }

    public B getRight() {
        return getB();
    }

    public A getFirst() {
        return getA();
    }

    public B getSecond() {
        return getB();
    }

    public Object[] toArray() {
        return new Object[]{a, b};
    }
}