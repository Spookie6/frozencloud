package com.github.spookie6.frozen.utils.render;

import cc.polyfrost.oneconfig.config.core.OneColor;

public enum Color {
    MINECRAFT_BLACK("0"),
    MINECRAFT_DARK_BLUE("00A"),
    MINECRAFT_DARK_GREEN("0A0"),
    MINECRAFT_DARK_AQUA("0AA"),
    MINECRAFT_DARK_RED("A00"),
    MINECRAFT_DARK_PURPLE("A0A"),
    MINECRAFT_GOLD("FA0"),
    MINECRAFT_GRAY("A"),
    MINECRAFT_DARK_GRAY("5"),
    MINECRAFT_BLUE("55F"),
    MINECRAFT_GREEN("5F5"),
    MINECRAFT_AQUA("5FF"),
    MINECRAFT_RED("F55"),
    MINECRAFT_LIGHT_PURPLE("F5F"),
    MINECRAFT_YELLOW("FF5"),
    MINECRAFT_WHITE("F");

    private OneColor color;

    Color(String hex) {
        this.color = new OneColor(hex);
        color.setAlpha(255);
    }

    public OneColor getColor() {
        return this.color;
    }
}
