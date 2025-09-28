package dev.frozencloud.frozen.utils.gui.overlays;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class IntegerConfigBinding {
    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;

    public IntegerConfigBinding(Supplier<Integer> getter, Consumer<Integer> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public int  get() {
        return getter.get();
    }

    public void set(int value) {
        setter.accept(value);
    }
}