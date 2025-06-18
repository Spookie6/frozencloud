package com.github.spookie6.overlaymanager.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.KeyBind;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.core.OneKeyBind;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.libs.universal.UKeyboard;
import com.github.spookie6.overlaymanager.OverlayManager;

public class ModConfig extends Config {
    public ModConfig() {
        super(new Mod("OverlayManager", ModType.HYPIXEL), "config.json");
        initialize();

        registerKeyBind(moveOverlaysKeybind, OverlayManager.guiOverlayEditor::open);
    }

    @KeyBind(
            name = "Open overlay editor",
            description = "Opens the overlay locations editor gui"
    )
    public static OneKeyBind moveOverlaysKeybind = new OneKeyBind(UKeyboard.KEY_RCONTROL);

    @Switch(
            name = "Show overlay",
            description = "Yes"
    )
    public static boolean showOverlay = false;

    @Switch(
            name = "Show other overlay",
            description = "Yes"
    )
    public static boolean showOtherOverlay = false;

    @Text(
            name = "Overlay value"
    )
    public static String overlayValue = "Hello World";
}
