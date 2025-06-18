package com.github.spookie6.overlaymanager;

import com.github.spookie6.overlaymanager.commands.MoveOverlays;
import com.github.spookie6.overlaymanager.config.ModConfig;
import com.github.spookie6.overlaymanager.utils.overlays.BooleanConfigBinding;
import com.github.spookie6.overlaymanager.utils.overlays.GuiOverlayEditor;
import com.github.spookie6.overlaymanager.utils.overlays.OverlayConfigManager;
import com.github.spookie6.overlaymanager.utils.overlays.TextOverlay;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "@ID@", useMetadata=true)
public class OverlayManager {
    public static final String MODID = "@ID@";
    public static final String VERSION = "@VER@";

    public static final GuiOverlayEditor guiOverlayEditor = new GuiOverlayEditor();

    public static ModConfig config;

    public static final Minecraft mc = Minecraft.getMinecraft();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(guiOverlayEditor);
        MinecraftForge.EVENT_BUS.register(this);

        ClientCommandHandler.instance.registerCommand(new MoveOverlays());
        config = new ModConfig();

        OverlayConfigManager.init();
        this.initOverlays();
    }

    @Mod.EventHandler
    public void onClientStopping(FMLServerStoppedEvent e) {
        OverlayConfigManager.saveOverlayConfigs();
    };

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (OverlayManager.guiOverlayEditor.opened) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            com.github.spookie6.overlaymanager.utils.overlays.OverlayManager.renderOverlays();
        }
    }

    private void initOverlays() {
        com.github.spookie6.overlaymanager.utils.overlays.OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.showOverlay,
                        (val) -> ModConfig.showOverlay = val
                ),
                "Hello World",
                () -> ModConfig.overlayValue,
                () -> true,
                "Hello World"
        ));

//        com.github.spookie6.overlaymanager.utils.overlays.OverlayManager.register(new TextOverlay(
//                new BooleanConfigBinding(
//                        () -> ModConfig.showOtherOverlay,
//                        (val) -> ModConfig.showOtherOverlay = val
//                ),
//                "Your Mom",
//                () -> "Your Mom",
//                () -> true,
//                "Your Mom"
//        ));

        com.github.spookie6.overlaymanager.utils.overlays.OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.showOtherOverlay,
                        (val) -> ModConfig.showOtherOverlay = val
                ),
                "Clock",
                () -> String.valueOf(Minecraft.getSystemTime()),
                () -> true,
                "000000000000"
        ));
    }
}
