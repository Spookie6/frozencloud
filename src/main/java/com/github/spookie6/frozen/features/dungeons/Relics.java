package com.github.spookie6.frozen.features.dungeons;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.utils.gui.overlays.BooleanConfigBinding;
import com.github.spookie6.frozen.utils.gui.overlays.OverlayManager;
import com.github.spookie6.frozen.utils.gui.overlays.TextOverlay;
import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.events.impl.ServerTickEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.render.Renderer;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.github.spookie6.frozen.Frozen.mc;

public class Relics {
    private static int ticks = -1;
    private static long p5Start = -1;
    private static boolean inP5 = false;

    private static long relicsSpawned = -1;
    private static long pickedUp = -1;
    private static long placed = -1;

    private DungeonEnums.Relic currentRelic = DungeonEnums.Relic.NONE;

    HashMap<DungeonEnums.Relic, Long> relicTimes = new HashMap<DungeonEnums.Relic, Long>();

    public Relics() {
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.relicTimer,
                        (val) -> ModConfig.relicTimer = val
                ),
                "Relic Timer",
                this::getText,
                () -> inP5,
                "2.10"
        ));
        OverlayManager.register(new TextOverlay(
                new BooleanConfigBinding(
                        () -> ModConfig.debugOverlays,
                        (val) -> ModConfig.debugOverlays = val
                ),
                "Relics Debug Overlay",
                () -> "Relic: " + (currentRelic == null ? "No relic" : currentRelic.name) + "\nPlaced: " + placed + "\np5Start: " + p5Start + "\nTicks: " + ticks,
                () -> true,
                "Relics debug overlay"
        ));
    }

    private String getText() {
        if (ticks > 0) return String.format("%.2f", (float) ticks / 20);
        return "";
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onChatPacket(ChatPacketEvent e) {
        if (!DungeonUtils.getInDungeon()) return;
        Pattern p = Pattern.compile("\\[BOSS] Necron: All this, for nothing\\.\\.\\.");
        if (p.matcher(e.message).find()) {
            ticks = 42;
            inP5 = true;
            p5Start = System.currentTimeMillis();
        }

        Pattern pa = Pattern.compile(mc.thePlayer.getDisplayNameString() + " picked the Corrupted (\\w+) Relic!");
        Matcher m = pa.matcher(e.message);

        if (m.find()) {
            String relicName = m.group(1);
            DungeonEnums.Relic relic = DungeonEnums.Relic.getRelicByName(relicName);

            currentRelic = relic;
            pickedUp = System.currentTimeMillis();
        }
    }

//    Place Own Relic
    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.world == null || event.entityPlayer == null) return;
        if (!inP5 || currentRelic == DungeonEnums.Relic.NONE) return;
        if (!event.action.equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)) return;

        onCauldronInteract(event.pos, event);
    }

    @SubscribeEvent
    public void onPlayerBreaking(PlayerEvent.BreakSpeed event) {
        if (mc.theWorld == null || event.entityPlayer == null) return;
        if (!inP5 || currentRelic == DungeonEnums.Relic.NONE) return;

        onCauldronInteract(event.pos, event);
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent e) {
        if (ticks > -1) ticks--;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || mc.theWorld == null || !inP5) return;
        if (mc.theWorld.getLoadedEntityList().isEmpty()) return;

        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (!(entity instanceof EntityArmorStand)) continue;
            EntityArmorStand armorStand = (EntityArmorStand) entity;

            ItemStack helmet = armorStand.getEquipmentInSlot(4);
            if (helmet != null && ItemUtils.getSkyBlockID(helmet) != null && ItemUtils.getSkyBlockID(helmet).contains("RELIC")) {

                // First relic spawn detection
                if (relicsSpawned <= 0) {
                    relicsSpawned = System.currentTimeMillis();
                }

                // Placement detection
                for (DungeonEnums.Relic relic : DungeonEnums.Relic.values()) {
                    if (!relicTimes.containsKey(relic)) { // not placed yet
                        if (relic.cauldronPos.distanceSq(armorStand.getPosition()) < 4) {
                            relicTimes.put(relic, System.currentTimeMillis() - p5Start);
                        }
                    }
                }
            }
        }

        if (relicTimes.size() == 5 && placed > 0 && ModConfig.sendRelicTimes) {
            sendRelicMessages();
            ticks = -1;
            pickedUp = -1;
            relicsSpawned = -1;
            p5Start = -1;
            inP5 = false;
            currentRelic = DungeonEnums.Relic.NONE;
            relicTimes.clear();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        ticks = -1;
        pickedUp = -1;
        p5Start = -1;
        relicsSpawned = -1;
        inP5 = false;
        currentRelic = DungeonEnums.Relic.NONE;
        relicTimes.clear();
    }

    private void sendRelicMessages() {
        Map<DungeonEnums.Relic, Long> sorted = this.relicTimes.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (DungeonEnums.Relic teammateRelic : sorted.keySet()) {
            ChatUtils.sendModInfo(String.format("&%s%s &7Relic placed in &a%.2fs.", teammateRelic.colorCode, teammateRelic.name, (float) relicTimes.get(teammateRelic) / 1000));
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent e) {
        if (mc.theWorld == null || currentRelic == DungeonEnums.Relic.NONE) return;

        if (ModConfig.cauldronHighlight == 1) {
            Renderer.drawOutlinedBlock(currentRelic.cauldronPos, currentRelic.color, false, true, 5F);
        }
        if (ModConfig.cauldronHighlight == 2) {
            Renderer.drawFilledBlock(currentRelic.cauldronPos, currentRelic.color, false);
        }
    }

    public void onCauldronInteract(BlockPos pos, Event event) {
        String itemID = ItemUtils.getSkyBlockID(ItemUtils.getHeldItem());
        if (itemID == null || !itemID.contains("RELIC") ||  mc.thePlayer.inventory.getStackInSlot(8) != ItemUtils.getHeldItem()) return;

        Block block = mc.theWorld.getBlockState(pos).getBlock();

        if (block != Blocks.cauldron && block != Blocks.anvil) return;

        if (pos.equals(currentRelic.cauldronPos) || pos.equals(currentRelic.cauldronPos.down())) {
            placed = System.currentTimeMillis();
            if (ModConfig.sendRelicTimes) {
                relicTimes.put(currentRelic, System.currentTimeMillis() - p5Start);
                ChatUtils.sendModInfo(String.format("&%s%s Relic &7placed in &a%.2fs.", currentRelic.colorCode, currentRelic.name, (float) (placed - pickedUp) / 1000));
                ChatUtils.sendModInfo(String.format("&7Relic spawned in &a%.2fs.", (float) (relicsSpawned - p5Start) / 1000));
                ChatUtils.sendModInfo(String.format("&7Relic picked up in &a%.2fs.", (float) (pickedUp - relicsSpawned) / 1000));
                ChatUtils.sendModInfo(String.format("&7Relic placed &a%.2fs &7into P5.", (float) (placed - p5Start) / 1000));
            }
            currentRelic = DungeonEnums.Relic.NONE;
        } else if (ModConfig.relicsBlockIncorrect) {
            event.setCanceled(true);
            ChatUtils.sendModInfo("Wrong Cauldron! dw tho, i gotchu pookie <3");
        }
    }
}
