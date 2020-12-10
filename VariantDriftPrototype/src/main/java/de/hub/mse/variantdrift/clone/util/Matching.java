package de.hub.mse.variantdrift.clone.util;

import java.util.HashMap;
import java.util.Map;

public class Matching<T, U> {
    Map<T, U> tToU = new HashMap<>();
    Map<U, T> uToT = new HashMap<>();

    public void putMatchPair(T first, U second) {
        tToU.put(first,second);
        uToT.put(second,first);
    }

    public T getFirst(U second) {
        return uToT.get(second);
    }

    public U getSecond(T first) {
        return tToU.get(first);
    }
}
