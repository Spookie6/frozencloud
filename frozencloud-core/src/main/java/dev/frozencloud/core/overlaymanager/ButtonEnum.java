package dev.frozencloud.core.overlaymanager;

import java.util.Arrays;

public enum ButtonEnum {
    MOUSE_LEFT (0),
    MOUSE_RIGHT(1),
    MOUSE_MIDDLE(2),
    MOUSE_BACKWARD(3),
    MOUSE_FORWARD(4);

    private final int value;

    ButtonEnum(int value) {
        this.value = value;
    }

    public static ButtonEnum getButton(int value) {
        return Arrays.stream(ButtonEnum.values()).filter(x -> x.value == value).findFirst().orElse(null);
    }
}