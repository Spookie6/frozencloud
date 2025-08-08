package com.github.spookie6.frozen;

import com.github.spookie6.frozen.commands.MainCommand;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.EventDispatcher;
import com.github.spookie6.frozen.features.dungeons.*;
import com.github.spookie6.frozen.features.hud.MaskTimers;
import com.github.spookie6.frozen.features.hud.ReaperTimer;
import com.github.spookie6.frozen.features.hud.Speed;
import com.github.spookie6.frozen.features.dungeons.TerminalTitles;
import com.github.spookie6.frozen.features.misc.*;
import com.github.spookie6.frozen.utils.SlotBindingUtils;
import com.github.spookie6.frozen.utils.gui.overlays.*;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import com.github.spookie6.frozen.utils.skyblock.PartyUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

@Mod(modid = "@ID@", useMetadata=true)
public class Frozen {
    public static final String MODID = "@ID@";
    public static final String VERSION = "@VER@";

    public static final GuiOverlayEditor guiOverlayEditor = new GuiOverlayEditor();

    public static ModConfig config;

    public static final Minecraft mc = Minecraft.getMinecraft();

    public static final String chatPrefix = "&f[&bFrozen&f] &8»&7 ";

    private final ArrayList<Object> modules = new ArrayList<>();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        OverlayConfigManager.init();
        SlotBindingUtils.init();
        MinecraftForge.EVENT_BUS.register(guiOverlayEditor);
        MinecraftForge.EVENT_BUS.register(this);

        ClientCommandHandler.instance.registerCommand(new MainCommand());
        config = new ModConfig();

        addModules();
        modules.forEach(MinecraftForge.EVENT_BUS::register);

        initOverlays();
    }

    @Mod.EventHandler
    public void onClientStopping(FMLServerStoppedEvent e) {
        OverlayConfigManager.saveOverlayConfigs();
    };

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (Frozen.guiOverlayEditor.opened) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            OverlayManager.renderOverlays();
        }
    }

    private void addModules() {
        Collections.addAll(modules,
                new EventDispatcher(),
                new LocationUtils(),
                new DungeonUtils(),
                new PartyUtils(),
                new PlayerRenderer(),
                new SplitsManager(),
                new ExplosiveShot(),
                new TickTimers(),
                new Speed(),
                new MaskTimers(),
                new ReaperTimer(),
                new WarpCooldown(),
                new HiderAfterLeap(),
                new AutoDraft(),
                new Refills(),
                new Splits(),
                new CrystalTitle(),
                new SlotBinding(),
                new Gui(),
                new TerminalTitles(),
                new LegacyAxes()
        );
    }

    private void initOverlays() {
        com.github.spookie6.frozen.utils.gui.overlays.OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.timeHud,
                        (val) -> ModConfig.timeHud = val
                ),
                "Clock",
                () -> {
                    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    return now.format(timeFormat);
                },
                () -> true,
                "00:00:00"
        ));
    }

//    @SubscribeEvent
//    public void onWorldRenderLast(RenderWorldLastEvent e) {
//        Renderer.drawOutlinedBlock(new BlockPos(1, 5, 1), new OneColor(255, 255, 255), false, true, 3);
//
//        for (Entity entity : mc.theWorld.loadedEntityList) {
//            Renderer.drawEntityAABB(entity, new OneColor(255, 155, 155), true, true, 2, e.partialTicks);
//        }
//    }
}
