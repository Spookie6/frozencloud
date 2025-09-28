package dev.frozencloud.frozen.utils.gui.overlays;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class BooleanConfigBinding {
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    public BooleanConfigBinding(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    public boolean get() {
        return getter.get();
    }

    public void set(boolean value) {
        setter.accept(value);
    }

    public void toggle() {
        set(!get());
    }
}