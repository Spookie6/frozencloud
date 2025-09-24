package com.github.spookie6.frozen.utils.skyblock.dungeon;

import com.github.spookie6.frozen.utils.render.Color;

public enum Rarity {
    ADMIN(Color.MINECRAFT_DARK_RED),
    ULTIMATE(Color.MINECRAFT_DARK_RED),
    VERY_SPECIAL(Color.MINECRAFT_RED),
    SPECIAL(Color.MINECRAFT_RED),
    DIVINE(Color.MINECRAFT_AQUA),
    MYTHIC(Color.MINECRAFT_LIGHT_PURPLE),
    LEGENDARY(Color.MINECRAFT_GOLD),
    EPIC(Color.MINECRAFT_DARK_PURPLE),
    RARE(Color.MINECRAFT_BLUE),
    UNCOMMON(Color.MINECRAFT_GREEN),
    COMMON(Color.MINECRAFT_WHITE),
    UNKNOWN(Color.MINECRAFT_BLACK);

    private Color color;

    Rarity(Color color) {
        this.color = color;
    }

    public Color getColor() {return this.color;}

    public static Rarity getRarityByString(String input) {
        if (input == null || input.isEmpty()) return UNKNOWN;
        switch (input) {
            case "ADMIND": return ADMIN;
            case "ULTIMATE": return ULTIMATE;
            case "VERY_SPECIAL": return VERY_SPECIAL;
            case "SPECIAL": return SPECIAL;
            case "DIVINE": return DIVINE;
            case "MYTHIC": return MYTHIC;
            case "LEGENDARY": return LEGENDARY;
            case "EPIC": return EPIC;
            case "RARE": return RARE;
            case "UNCOMMON": return UNCOMMON;
            case "COMMON": return COMMON;
        }
        return UNKNOWN;
    }

    public static Rarity getRarityByColorCode(String colorCode) {
        if (colorCode == null || colorCode.isEmpty()) return UNKNOWN;
        switch (colorCode) {
            case "b": return DIVINE;
            case "d": return MYTHIC;
            case "6": return LEGENDARY;
            case "5": return EPIC;
            case "9": return RARE;
            case "a": return UNCOMMON;
            case "f": return COMMON;
        }

        return UNKNOWN;
    }
}
