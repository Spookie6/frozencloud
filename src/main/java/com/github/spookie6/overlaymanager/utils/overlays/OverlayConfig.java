package com.github.spookie6.overlaymanager.utils.overlays;

import java.awt.*;

public class OverlayConfig {
    public int x, y = 0;
    public double scale = 1.0;
    public int color = new Color(255, 255, 255).getRGB();
    public boolean shadow = false;

    public OverlayConfig() {}

//    public OverlayConfig(int x, int y, double scale, Color color) {
//        this.x = x;
//        this.y = y;
//        this.scale = scale;
//        this.color = color.getRGB();
//    }
}