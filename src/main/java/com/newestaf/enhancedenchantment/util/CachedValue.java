package com.newestaf.enhancedenchantment.util;

import java.util.function.Supplier;

public class CachedValue<T> {

    private Supplier<T> supplier;
    private T value;

    public CachedValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (supplier != null) {
            value = supplier.get();
            supplier = null;
        }

        return value;
    }

}
