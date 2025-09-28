package dev.frozencloud.frozen.utils.gui.overlays;

import java.util.ArrayList;
import java.util.List;

import static dev.frozencloud.frozen.Frozen.mc;

public class OverlayManager {
    private static final List<Overlay> overlays = new ArrayList<>();

    public static void register(Overlay overlay) {
        for (Overlay o : getOverlays()) {
            if (o.displayName.trim().toLowerCase().equals(overlay.displayName)) throw new RuntimeException("Multiple overlays with duplicate display names not allowed!"); // Safeguard for development. (should) will never crash user.
        }
        overlays.add(overlay);
    }

    public static void renderOverlays() {
        for (Overlay overlay : overlays) {
            if (!overlay.isVisible()) continue;
            overlay.render(mc);
        }
    }

    public static List<Overlay> getOverlays() {
        return overlays;
    }

    public static Overlay getOverlay(String name) {
        return getOverlays().stream().filter(x -> x.configName.equals(name)).findFirst().orElse(null);
    }
}
