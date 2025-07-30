package com.github.spookie6.frozen.utils.skyblock;

import static com.github.spookie6.frozen.Frozen.mc;

public class StringUtils {

    public static String stripAliens(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (mc.fontRendererObj.getCharWidth(c) > 0 || c == '§')
                sb.append(c);
        }
        return sb.toString();
    }
}
