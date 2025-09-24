package com.github.spookie6.frozen;

import com.github.spookie6.frozen.commands.MainCommand;
import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.EventDispatcher;
import com.github.spookie6.frozen.features.dungeons.*;
import com.github.spookie6.frozen.features.hud.*;
import com.github.spookie6.frozen.features.dungeons.TerminalTitles;
import com.github.spookie6.frozen.features.misc.*;
import com.github.spookie6.frozen.utils.SlotBindingUtils;
import com.github.spookie6.frozen.utils.gui.overlays.*;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import com.github.spookie6.frozen.utils.skyblock.PartyUtils;
import com.github.spookie6.frozen.utils.skyblock.ScoreboardUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Mod(modid = "@ID@", useMetadata=true)
public class Frozen {
    public static final String MODID = "@ID@";
    public static final String VERSION = "@VER@";
    public static final String chatPrefix = "&f[&bFrozen&f] &8»&7 ";

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static ModConfig config;

    public static GuiScreen guiToOpen = null;
    public static final GuiOverlayEditor guiOverlayEditor = new GuiOverlayEditor();
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
        if (mc.currentScreen != null && mc.currentScreen.equals(guiOverlayEditor)) return;
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            OverlayManager.renderOverlays();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase.equals(TickEvent.Phase.END) || guiToOpen == null) return;

        mc.displayGuiScreen(guiToOpen);
        guiToOpen = null;
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
                new LegacyAxes(),
                new Relics(),
                new Dragons(),
                new LocationalMessage(),
                new ArmorHud(),
                new TerminalSplits(),
                new Notifications(),
                new BlockOverlay()
        );
    }

    private void initOverlays() {
        OverlayManager.register(new TextOverlay(
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

//        DEBUG OVERLAYS
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.debugOverlays,
                        (val) -> ModConfig.debugOverlays = val
                ),
                "Location Debug",
                () -> {
                    List<String> lines = new ArrayList<String>() {{
                        add("Hypixel: " + LocationUtils.isOnHypixel);
                        add("Gamemode: " + (LocationUtils.isInSkyblock ? "Skyblock" : "Other"));
                        add("Area: " + LocationUtils.currentArea);
                    }};
                    return String.join("\n", lines);
                },
                () -> true,
                "Location Debug Overlay"
        ));

        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.debugOverlays,
                        (val) -> ModConfig.debugOverlays = val
                ),
                "Dungeon Debug",
                () -> {
                    List<String> lines = new ArrayList<String>() {{
                        add("Floor: " + DungeonUtils.getFloor().toString());
                        add("MM: " + DungeonUtils.getFloor().isMM);
                        add("Players:");
                        add(DungeonUtils.getDungeonPlayers().stream().map(x -> x.username + " | " + x.clazz.toString() + " " + x.clazzLevel).collect(Collectors.joining("\n")));
                    }};
                    return String.join("\n", lines);
                },
                DungeonUtils::getInDungeon,
                "Dungeon Debug Overlay"
        ));

        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.debugOverlays,
                        (val) -> ModConfig.debugOverlays = val
                ),
                "Scoreboard Debug",
                () -> String.join("\n", ScoreboardUtils.getScoreboardLines()),
                () -> true,
                "Scoreboard Debug Overlay"
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
