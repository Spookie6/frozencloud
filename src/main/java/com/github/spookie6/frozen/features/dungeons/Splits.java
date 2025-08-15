package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.gui.overlays.*;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;

import java.util.ArrayList;
import java.util.List;

public class Splits {
    private static List<String> EXAMPLE_LINES = new ArrayList<String>() {{
        add("§4Blood Open§r#§a10m 00.00s§r#§8[§710m 00.00s§r§8]§r");
        add("§cBlood Clear§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§dPortal§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§9Boss Entry§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§5Maxor§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§3Storm§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§eTerminals§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§6Goldor§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§cNecron§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§4Dragons§r#§a0.00s§r#§8[§70.00s§r§8]§r");
        add("§bBoss§r#§a0.00s§r#§8[§70.00s§r§8]§r");
    }};

    public Splits() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.splits,
                        (val) -> ModConfig.splits = val
                ),
                "Splits",
                SplitsManager::getText,
                SplitsManager::isInitialized,
                String.join("\n", EXAMPLE_LINES)
        ).setRightAlign(new BooleanConfigBinding(() -> ModConfig.splitsRightAlign, (val) -> ModConfig.splitsRightAlign = val))
                .setExtraWidth(new IntegerConfigBinding(() -> ModConfig.splitsExtraWidth, (val) -> ModConfig.splitsExtraWidth = val)));

        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.bloodSplit,
                        (val) -> ModConfig.bloodSplit = val
                ),
                "Blood split",
                () -> {
                    long time = SplitsManager.getSplitTime(SplitsManager.Split.BloodCleared)[0];
                    return String.format("%.2f", (float) time / 1000);
                },
                () -> {
                    if (SplitsManager.currentSplit.equals(SplitsManager.Split.BloodCleared)) {
                        return (DungeonUtils.getCurrentDungeonPlayer() != null && DungeonUtils.getCurrentDungeonPlayer().clazz.equals(DungeonEnums.Class.MAGE));
                    }
                    return false;
                },
                "0.00"
        ));
    }
}
