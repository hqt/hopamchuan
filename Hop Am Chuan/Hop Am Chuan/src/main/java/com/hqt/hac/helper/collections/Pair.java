package com.hqt.hac.helper.collections;

/**
 * Created by ThaoHQSE60963 on 12/24/13.
 */

/**
 * Stimulate Pair in C++
 * Using Generic for General
 */
public class Pair<A,B> {
    A a;
    B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
