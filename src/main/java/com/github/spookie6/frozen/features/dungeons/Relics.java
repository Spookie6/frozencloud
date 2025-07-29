//package com.github.spookie6.frozen.features.dungeons.dragons;
//
//import cc.polyfrost.oneconfig.config.core.OneColor;
//import cc.polyfrost.oneconfig.events.event.Stage;
//import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
//import com.github.spookie6.frozen.utils.skyblock.ItemUtils;
//import net.minecraft.block.Block;
//import net.minecraft.client.Minecraft;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.item.EntityArmorStand;
//import net.minecraft.init.Blocks;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.BlockPos;
//import net.minecraft.util.Vec3;
//import net.minecraftforge.client.event.RenderWorldLastEvent;
//import net.minecraftforge.event.entity.player.PlayerInteractEvent;
//import net.minecraftforge.event.world.WorldEvent;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import com.github.spookie6.frozen.config.Dragons;
//import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
//import com.github.spookie6.frozen.events.impl.ServerTickEvent;
//import com.github.spookie6.frozen.utils.ChatUtils;
//import com.github.spookie6.frozen.utils.render.Color;
//import com.github.spookie6.frozen.utils.render.Renderer;
//import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
//import com.github.spookie6.frozen.wrappers.SingleTextHud;
//import net.minecraftforge.fml.common.gameevent.TickEvent;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import static com.github.spookie6.frozen.Frozen.mc;
//
//public class Relics {
//    private static int ticks = -1;
//    private static long p5Start = -1;
//    private static boolean inP5 = false;
//
//    private static long relicsSpawned = -1;
//    private static long pickedUp = -1;
//    private static long placed = -1;
//
//    private Relic currentRelic = null;
//
//    HashMap<Relic, Long> relicTimes = new HashMap<Relic, Long>();
//
//    public static class RelicTimer extends SingleTextHud {
//        public RelicTimer() {
//            super("", false, 0, 0, 3, false, false, 0, 0, 0, new OneColor(255,105,180, 80), false, 2, new OneColor(0, 0, 0));
//        }
//
//        @Override
//        protected String getText(boolean example) {
//            if (example) return String.format("%.2f", (float) 42 / 20);
//
//            if (ticks > 0) return String.format("%.2f", (float) ticks / 20);
//            return "";
//        }
//    }
//
//    @SubscribeEvent(receiveCanceled = true)
//    public void onChatPacket(ChatPacketEvent e) {
//        Pattern p = Pattern.compile("\\[BOSS] Necron: All this, for nothing\\.\\.\\.");
//        if (p.matcher(e.message).find()) {
//            ticks = 42;
//            inP5 = true;
//            p5Start = Minecraft.getSystemTime();
//        }
//
//        Pattern pa = Pattern.compile("(\\w+) picked the Corrupted (\\w+) Relic!");
//        Matcher m = pa.matcher(e.message);
//
//        if (m.find()) {
//            String username = m.group(1);
//            String relicName = m.group(2);
//            Relic relic = Relic.getRelicByName(relicName);
//
//            if (username.equals(Minecraft.getMinecraft().thePlayer.getName())) currentRelic = relic;
//            else relicTimes.put(relic, Minecraft.getSystemTime());
//        }
//    }
//
////    Place Own Relic
//    @SubscribeEvent
//    public void onPlayerInteract(PlayerInteractEvent event) {
//        if (event.world == null || event.entityPlayer == null) return;
//        if (!inP5 || currentRelic == Relic.NONE) return;
//
//        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
//        if (heldItem == null || (!heldItem.getDisplayName().contains("Relic") && !heldItem.getDisplayName().contains("Skyblock Menu"))) return;
//
//        BlockPos pos = event.pos;
//        Block block = event.world.getBlockState(pos).getBlock();
//
//        if (block != Blocks.cauldron && block != Blocks.anvil) return;
//
//        if (!pos.equals(currentRelic.cauldronPos) && !pos.equals(currentRelic.cauldronPos.down()) && Dragons.blockIncorrect) {
//            event.setCanceled(true);
//            ChatUtils.sendModInfo("Wrong Cauldron! dw tho, i gotchu pookie <3");
//            return;
//        }
//
//        placed = Minecraft.getSystemTime();
//        if (Dragons.sendRelictime) {
//            ChatUtils.sendModInfo(String.format("%s%s relic &7placed in &a%.2fs.", currentRelic.colorCode, currentRelic.name, (float) (placed - pickedUp) / 1000));
//            ChatUtils.sendModInfo(String.format("&7Relic spawned in &a%.2fs.", (float) (relicsSpawned - p5Start) / 1000));
//            ChatUtils.sendModInfo(String.format("&7Relic picked up in &a%.2fs.", (float) (pickedUp - relicsSpawned) / 1000));
//            ChatUtils.sendModInfo(String.format("&7Relic placed &a%.2fs into P5.", (float) (placed - p5Start) / 1000));
//        }
//    }
//
//    @SubscribeEvent
//    public void onServerTick(ServerTickEvent e) {
//        if (ticks > 0) ticks--;
//    }
//
//    @SubscribeEvent
//    public void onTick(TickEvent.ClientTickEvent e) {
//        if (e.phase.equals(TickEvent.Phase.END) || relicsSpawned > 0) return;
//
//        if (Minecraft.getMinecraft().theWorld == null) return;
//        if (!DungeonUtils.getInDungeon() || !DungeonUtils.getFloor().isFloor(7)) return;
//        if (Minecraft.getMinecraft().theWorld.getLoadedEntityList().isEmpty()) return;
//
//        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
//            if (!(entity instanceof EntityArmorStand)) return;
//            EntityArmorStand armorStand = (EntityArmorStand) entity;
//
//            if (armorStand.getEquipmentInSlot(4) != null && ItemUtils.getSkyBlockID(armorStand.getEquipmentInSlot(4)).contains("RELIC")) {
//                if (relicsSpawned <= 0) {
//                    relicsSpawned = Minecraft.getSystemTime();
//                    break;
//                } else {
//                    for (Relic relic : Relic.values()) {
//                        if (relicTimes.get(relic) == null) continue;
//                        if (relic.cauldronPos.distanceSq(armorStand.getPosition()) < 4) {
//                            relicTimes.put(relic, Minecraft.getSystemTime() - p5Start);
//                        }
//                    }
//                }
//            }
//        }
//        if (relicTimes.size() >= 4 && placed > 0 && Dragons.sendRelictime) sendRelicMessages();
//    }
//
//    @SubscribeEvent
//    public void onWorldUnload(WorldEvent.Unload e) {
//        ticks = -1;
//        pickedUp = -1;
//        p5Start = -1;
//        inP5 = false;
//        currentRelic = Relic.NONE;
//        relicTimes.clear();
//    }
//
//    private void sendRelicMessages() {
//        for (Relic teammateRelic : relicTimes.keySet()) {
//            ChatUtils.sendModInfo(String.format("%s%s&7Relic placed in &a%.2fs.", teammateRelic.colorCode, teammateRelic.name, (float) relicTimes.get(teammateRelic) / 1000));
//        }
//    }
//
//    @SubscribeEvent
//    public void onRenderWorldLast(RenderWorldLastEvent e) {
//        if (Minecraft.getMinecraft().theWorld == null
//         || !DungeonUtils.getFloor().isFloor(7)) return;
//
//        if (Dragons.cauldronHighlight == 1) {
//            Renderer.drawFilledBlock(currentRelic.cauldronPos, currentRelic.color, false);
//        }
//        if (Dragons.cauldronHighlight == 2) {
//            Renderer.drawOutlinedBlock(currentRelic.cauldronPos, currentRelic.color, false, true, 5F);
//        }
//    }
//
//    enum Relic {
//        GREEN("GREEN_KING_RELIC", "Green", 'a', Color.MINECRAFT_GREEN.getColor(), new Vec3(20.5, 6.5, 94.5), new BlockPos(49.0, 7.0, 44.0)),
//        PURPLE("PURPLE_KING_RELIC", "Purple", '5', Color.MINECRAFT_DARK_PURPLE.getColor(),  new Vec3(56.5, 8.5, 132.5), new BlockPos(54.0, 7.0, 41.0)),
//        BLUE("BLUE_KING_RELIC", "Blue",'b', Color.MINECRAFT_BLUE.getColor(), new Vec3(91.5, 6.5, 94.5), new BlockPos(59.0, 7.0, 44.0)),
//        ORANGE("ORANGE_KING_RELIC", "Orange", '6', Color.MINECRAFT_GOLD.getColor(), new Vec3(90.5, 6.5, 56.5), new BlockPos(57.0, 7.0, 42.0)),
//        RED("RED_KING_RELIC", "Red", 'c', Color.MINECRAFT_RED.getColor(), new Vec3(22.5, 6.5, 59.5), new BlockPos(51.0, 7.0, 42.0)),
//        NONE("", "", 'f', Color.MINECRAFT_WHITE.getColor(), new Vec3(0, 0, 0), new BlockPos(0, 0, 0));
//
//        final String id;
//        final String name;
//        final char colorCode;
//        final OneColor color;
//        final Vec3 spawnPos;
//        final BlockPos cauldronPos;
//
//        Relic(String id, String name, char colorCode, OneColor color, Vec3 spawnPos, BlockPos cauldronPos) {
//            this.id = id;
//            this.name = name;
//            this.colorCode = colorCode;
//            this.color = color;
//            this.spawnPos = spawnPos;
//            this.cauldronPos = cauldronPos;
//        }
//
//        public static Relic getRelicByName(String name) {
//            for (Relic relic : Relic.values()) {
//                if (relic.id.toLowerCase().contains(name.toLowerCase())) return relic;
//            }
//            return Relic.NONE;
//        }
//    }
//}
