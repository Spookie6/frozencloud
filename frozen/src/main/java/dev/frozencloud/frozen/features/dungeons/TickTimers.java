package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.core.ModEnum;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.ChatPacketEvent;
import dev.frozencloud.frozen.events.impl.ServerTickEvent;
import dev.frozencloud.core.overlaymanager.BooleanConfigBinding;
import dev.frozencloud.core.overlaymanager.OverlayManager;
import dev.frozencloud.core.overlaymanager.TextOverlay;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class TickTimers {
    private int crystalTicks = -1;
    private int padTicks = -1;
    private int crushTicks = -1;
    private long startTime = -1;
    private int barrierTicks = -1;

    public TickTimers() {
//        Registering overlays
        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.crystalTicks,
                        (val) -> ModConfig.crystalTicks = val
                ),
                "Energy crystal spawn ticks",
                () -> String.format("%.2f", (float) crystalTicks / 20),
                () -> crystalTicks > -1,
                "1.80"
            )
        );

        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.padTicks,
                        (val) -> ModConfig.padTicks = val
                ),
                "Storm pad ticks",
                () -> String.format("%.2f", (float) padTicks / 20),
                () -> padTicks > -1,
                "1.00"
            )
        );

        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.crushTicks,
                        (val) -> ModConfig.crushTicks = val
                ),
                "Storm crush ticks",
                () -> String.format("%.2f", (float) crushTicks / 20),
                () -> crushTicks > -1,
                "1.00"
            )
        );

        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.startTimer,
                        (val) -> ModConfig.startTimer = val
                ),
                "Goldor start timer",
                this::getStartTimer,
                () -> startTime > -1,
                "5.20"
            )
        );

        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.barrierTicks,
                        (val) -> ModConfig.barrierTicks = val
                ),
                "Goldor barrier ticks",
                this::getBarrierTimer,
                () -> barrierTicks > -1,
                "3.00"
                )
        );
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatReceived(ChatPacketEvent e) {
        if (e.message.matches("^\\[BOSS] Maxor: THAT BEAM! IT HURTS! IT HURTS!!$") || e.message.matches("^\\[BOSS] Maxor: YOU TRICKED ME!$")) crystalTicks = 36;
        if (e.message.matches("^\\[BOSS] Storm: Pathetic Maxor, just like expected\\.$")) padTicks = 20;
        if (e.message.matches("^\\[BOSS] Storm: I should have known that I stood no chance\\.$")) {
            padTicks = -1;
            startTime = System.currentTimeMillis() + 5200;
        }
        if (e.message.matches("^\\[BOSS] Storm: Oof") || e.message.matches("\\[BOSS] Storm: Ouch, that hurt!$")) crushTicks = 20;
        if (e.message.matches("^\\[BOSS] Goldor: Who dares trespass into my domain\\?$")) barrierTicks = 60;
        if (e.message.matches("^The Core entrance is opening!$")) barrierTicks = -1;
    }

    @SubscribeEvent
    public void onServerTicks(ServerTickEvent event) {
        if (crystalTicks >= 0) crystalTicks--;
        if (crushTicks >= 0) crushTicks--;

        if (padTicks > -1) {
            padTicks--;
            if (padTicks == 0) padTicks = 20;
        }
        if (barrierTicks > -1) {
            barrierTicks--;
            if (barrierTicks == 0) barrierTicks = 60;
        }
    }

    private String getStartTimer() {
        if (startTime < 0) return "";
        long remaining = startTime - System.currentTimeMillis();
        if (remaining < 0) startTime = -1;
        return String.format("%.2f", (float) remaining / 1000);
    }

    private String getBarrierTimer() {
        if (ModConfig.barrierTicksDynamicColors) {
            String prefix = barrierTicks > 40 ? "&a" : barrierTicks > 20 ? "&d" : "&c";
            return (prefix + String.format("%.2f", (float) barrierTicks / 20));
        }
        return String.format("%.2f", (float) barrierTicks / 20);
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {reset();}

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e ) {reset();}

    private void reset() {
        this.crystalTicks = -1;
        this.padTicks = -1;
        this.crushTicks = -1;
        this.startTime = -1;
        this.barrierTicks = -1;
    }
}
