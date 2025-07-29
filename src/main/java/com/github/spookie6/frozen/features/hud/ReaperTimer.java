package com.github.spookie6.frozen.features.hud;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.overlays.BooleanConfigBinding;
import com.github.spookie6.frozen.utils.overlays.OverlayManager;
import com.github.spookie6.frozen.utils.overlays.TextOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import static com.github.spookie6.frozen.Frozen.mc;

public class ReaperTimer  {

    public ReaperTimer() {
        OverlayManager.register(new TextOverlay(
                        new BooleanConfigBinding(
                                () -> ModConfig.reaperTimer,
                                (val) -> ModConfig.reaperTimer = val
                        ),
                        "Reaper timer",
                        () -> String.format("%.2f", (float) (reaperUsed + 6000 - System.currentTimeMillis()) /1000),
                        () -> reaperUsed + 6000 - System.currentTimeMillis() >= 0,
                        "6.00"
                )
        );
    }

    private long reaperUsed = -1;
    private boolean soundPlayed = false;

    @SubscribeEvent
    public void onSound(PlaySoundEvent e) {
        if (e.name.equals("mob.zombie.remedy")) {
            soundPlayed = true;
        };
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase.equals(TickEvent.Phase.END)) return;
        if (!soundPlayed) return;

        ItemStack chestplate = mc.thePlayer.getCurrentArmor(2);
        if (chestplate == null) return;

        if (ItemUtils.getSkyBlockID(chestplate).equals("REAPER_CHESTPLATE")) {
            if ( ((ItemArmor) chestplate.getItem()).getColor(chestplate) == 16711680) {
                reaperUsed = System.currentTimeMillis();
                soundPlayed = false;
            }
        } else soundPlayed = false;

        if (reaperUsed > 0 && reaperUsed + 6000 - System.currentTimeMillis() <= 0) reaperUsed = -1;
    }
}
