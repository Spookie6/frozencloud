package com.github.spookie6.frozen.utils.overlays;

import java.awt.*;

public class OverlayConfig {
    public int x, y = 0;
    public double scale = 1.0;
    public int color = new Color(255, 255, 255).getRGB();
    public boolean shadow = false;

    public OverlayConfig() {}
}