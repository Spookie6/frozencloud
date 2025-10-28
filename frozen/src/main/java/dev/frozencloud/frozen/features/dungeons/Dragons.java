package dev.frozencloud.frozen.features.dungeons;

import dev.frozencloud.core.ModEnum;
import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.DragonSpawnEvent;
import dev.frozencloud.frozen.events.impl.ServerTickEvent;
import dev.frozencloud.core.overlaymanager.BooleanConfigBinding;
import dev.frozencloud.core.overlaymanager.OverlayManager;
import dev.frozencloud.core.overlaymanager.TextOverlay;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonEnums;
import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Dragons {
    private int dragonSpawnTicks = -1;

    private long redSpawning = -1;
    private long blueSpawning = -1;
    private long greenSpawning = -1;
    private long orangeSpawning = -1;
    private long purpleSpawning = -1;

    private char testColorChar = 'r';

    private DungeonEnums.Dragon[] drags = {null, null};

    public Dragons() {
        OverlayManager.register(new TextOverlay(
                ModEnum.FROZEN,
                new BooleanConfigBinding(
                        () -> ModConfig.dragonTimer,
                        (val) -> ModConfig.dragonTimer = val
                ),
                "Dragon timer",
                this::getText,
                () -> dragonSpawnTicks > 0,
                "5.00"
        ));
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        if (ModConfig.dragonSpawnTimerDynamicColors && testColorChar != 'r') {
            sb.append("&");
            sb.append(testColorChar);
        }

        sb.append(String.format("%.2f", (float) dragonSpawnTicks / 20));
        return sb.toString();
    }

    private void assignTextColor(DungeonEnums.Dragon drag) {
        if (drags[0] == null) {
            drags[0] = drag;
        } else if (drags[1] == null && drag != drags[0]) {
            drags[1] = drag;
            determinePrio();
        } else {
            testColorChar = drag.colorCode;
        }
    }

    public void determinePrio() {
        DungeonEnums.Class clazz = DungeonUtils.getCurrentDungeonPlayer().clazz;
        if (clazz == null) return;

        if (clazz == DungeonEnums.Class.ARCHER || clazz == DungeonEnums.Class.TANK) {
            testColorChar = (drags[0].prio[0] < drags[1].prio[0]) ? drags[0].colorCode : drags[1].colorCode;
        } else if (clazz == DungeonEnums.Class.BERSERK || clazz == DungeonEnums.Class.MAGE || clazz == DungeonEnums.Class.HEALER) {
            testColorChar = (drags[0].prio[0] > drags[1].prio[0]) ? drags[0].colorCode : drags[1].colorCode;
        }
    }

    @SubscribeEvent
    public void onDragonSpawn(DragonSpawnEvent e) {
        long now = System.currentTimeMillis();
        switch (e.dragon) {
            case RED:
                if (redSpawning > 0) return;
                dragonSpawnTicks = 100;
                redSpawning = now;
                assignTextColor(DungeonEnums.Dragon.RED);
                break;
            case BLUE:
                if (blueSpawning > 0) return;
                dragonSpawnTicks = 100;
                blueSpawning = now;
                assignTextColor(DungeonEnums.Dragon.BLUE);
                break;
            case GREEN:
                if (greenSpawning > 0) return;
                dragonSpawnTicks = 100;
                greenSpawning = now;
                assignTextColor(DungeonEnums.Dragon.GREEN);
                break;
            case ORANGE:
                if (orangeSpawning > 0) return;
                dragonSpawnTicks = 100;
                orangeSpawning = now;
                assignTextColor(DungeonEnums.Dragon.ORANGE);
                break;
            case PURPLE:
                if (purpleSpawning > 0) return;
                dragonSpawnTicks = 100;
                purpleSpawning = now;
                assignTextColor(DungeonEnums.Dragon.PURPLE);
                break;
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent e) {
        if (dragonSpawnTicks >= 0) dragonSpawnTicks--;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        long now = System.currentTimeMillis();
        if (redSpawning > 0 && now - redSpawning >= 8000) redSpawning = -1;
        if (blueSpawning > 0 && now - blueSpawning >= 8000) blueSpawning = -1;
        if (greenSpawning > 0 && now - greenSpawning >= 8000) greenSpawning = -1;
        if (orangeSpawning > 0 && now - orangeSpawning >= 8000) orangeSpawning = -1;
        if (purpleSpawning > 0 && now - purpleSpawning >= 8000) purpleSpawning = -1;
    }

    @SubscribeEvent
    public void onWorldLeave(WorldEvent.Unload e) {
        dragonSpawnTicks = -1;
        redSpawning = -1;
        blueSpawning = -1;
        greenSpawning = -1;
        orangeSpawning = -1;
        purpleSpawning = -1;
        drags = new DungeonEnums.Dragon[]{null, null};
        testColorChar = 'r';
    }
}
