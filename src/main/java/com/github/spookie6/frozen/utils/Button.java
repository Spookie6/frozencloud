package com.github.spookie6.frozen.utils;

import java.util.Arrays;

public enum Button {
    MOUSE_LEFT (0),
    MOUSE_RIGHT(1),
    MOUSE_MIDDLE(2),
    MOUSE_BACKWARD(3),
    MOUSE_FORWARD(4);

    private final int value;

    Button(int value) {
        this.value = value;
    }

    public static Button getButton(int value) {
        return Arrays.stream(Button.values()).filter(x -> x.value == value).findFirst().orElse(null);
    }
}