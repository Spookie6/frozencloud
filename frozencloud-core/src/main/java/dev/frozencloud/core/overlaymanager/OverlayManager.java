package dev.frozencloud.core.overlaymanager;

import dev.frozencloud.core.Core;
import dev.frozencloud.core.Mod;

import java.util.ArrayList;
import java.util.List;


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
            overlay.render(Core.INSTANCE.getMc());
        }
    }

    public static List<Overlay> getOverlays() {
        return overlays;
    }

    public static Overlay getOverlay(String name) {
        return getOverlays().stream().filter(x -> x.configName.equals(name)).findFirst().orElse(null);
    }
}
