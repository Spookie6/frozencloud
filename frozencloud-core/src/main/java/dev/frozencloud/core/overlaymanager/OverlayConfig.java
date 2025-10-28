package dev.frozencloud.core.overlaymanager;

import java.awt.*;

public class OverlayConfig {
    public int x, y = 0;
    public boolean centerX, centerY = false;
    public double scale = 1.0;
    public int color = new Color(255, 255, 255).getRGB();
    public boolean shadow = false;

//    Optional fields
    public Integer titleColor = null;

    public OverlayConfig() {}
}