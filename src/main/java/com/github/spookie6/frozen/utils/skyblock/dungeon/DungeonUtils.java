package com.github.spookie6.frozen.utils.skyblock.dungeon;

import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;

import com.github.spookie6.frozen.events.impl.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.github.spookie6.frozen.utils.StringUtils;
import com.github.spookie6.frozen.utils.skyblock.Island;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DungeonUtils {
    public static boolean getInDungeon() {return LocationUtils.currentArea.isArea(Island.Dungeon);}
    public static boolean getInBoss() {return (LocationUtils.currentDungeon != null && LocationUtils.currentDungeon.inBoss);}
    public static DungeonEnums.Floor getFloor() {return LocationUtils.currentDungeon != null ? LocationUtils.currentDungeon.floor : DungeonEnums.Floor.None;}

    public List<DungeonEnums.DungeonPlayer> getDungeonPlayers() {
        if (LocationUtils.currentDungeon == null) return new ArrayList<>();
        return LocationUtils.currentDungeon.dungeonPlayers;
    }

    public DungeonEnums.DungeonPlayer getCurrentDungeonPlayer() {
        return(getDungeonPlayers().stream()
                .filter(x -> x.username.equals(Minecraft.getMinecraft().thePlayer.getDisplayNameString()))
                .findFirst()
                .orElse(null));
    }

    public boolean isFloor(int n) {
        return getFloor().floorNumber == n;
    }

    public static DungeonEnums.M7Phases getF7Phase() {
        if (LocationUtils.currentDungeon == null || !LocationUtils.currentDungeon.floor.isFloor(7) || !getInBoss()) return DungeonEnums.M7Phases.UNKOWN;

        return SplitsManager.currentSplit.getM7Phase();
    }

    public double getMageCooldownMultiplier() {
        DungeonEnums.DungeonPlayer currentDungeonPlayer = getCurrentDungeonPlayer();
        if (currentDungeonPlayer == null || !currentDungeonPlayer.clazz.equals(DungeonEnums.Class.MAGE)) return 1.0;

        int amountOfMages = getDungeonPlayers().stream().filter(x -> x.clazz.equals(DungeonEnums.Class.MAGE)).collect(Collectors.toList()).size();
        return 1 - 0.25 - (Math.floor(currentDungeonPlayer.clazzLevel / 2.0) / 100) * (amountOfMages == 1 ? 2 : 1);
    }

    public long getAbilityCooldown(long baseCooldown) {
        return Math.round(baseCooldown * getMageCooldownMultiplier());
    }

    public static Pattern playerPattern = Pattern.compile("^\\[(\\d+)] (?:\\[\\w+] )*(\\w+) .*?\\((\\w+)(?: (\\w+))*\\)$");
    public static List<DungeonEnums.DungeonPlayer> getDungeonPlayers(List<DungeonEnums.DungeonPlayer> previousPlayers, List<String> tablist) {
        for (String line : tablist) {
            Matcher m = playerPattern.matcher(line);
            if (!m.find()) continue;

            List<String> groups = new ArrayList<>();
            for (int i = 0; i < m.groupCount() + 1; i++) {groups.add(m.group(i));}

            DungeonEnums.DungeonPlayer player = previousPlayers.stream().filter(x -> x.username.equals(groups.get(2))).findFirst().orElse(null);
            if (player == null) {
                String username = groups.get(2);
                String clazz = groups.get(3);
                String clazzlvl = groups.get(4);

                if (Minecraft.getMinecraft().theWorld == null) continue;
                EntityPlayer playerEntity = Minecraft.getMinecraft().theWorld.playerEntities.stream().filter(x -> x.getDisplayNameString().equals(username)).findFirst().orElse(null);
                if (playerEntity == null) continue;
                DungeonEnums.Class playerClazz = DungeonEnums.Class.getClass(clazz);
                if (playerClazz.isClass(DungeonEnums.Class.Unknown)) continue;

                DungeonEnums.DungeonPlayer dungeonPlayer = new DungeonEnums.DungeonPlayer(username, playerClazz, StringUtils.romanToDecimal(clazzlvl), playerEntity);
                previousPlayers.add(dungeonPlayer);
            } else {
                player.isDead = groups.get(3).equals("DEAD");
            }
        }
        return previousPlayers;
    }

    @SubscribeEvent
    public void onPacket(PacketEvent.Received e) {
        if (LocationUtils.currentDungeon != null) LocationUtils.currentDungeon.onPacket(e);
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinWorldEvent e) {
        if (LocationUtils.currentDungeon != null) LocationUtils.currentDungeon.onEntityJoin(e);
    }
}
