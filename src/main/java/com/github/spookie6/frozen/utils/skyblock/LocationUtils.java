package com.github.spookie6.frozen.utils.skyblock;

// I am good code stealer, thanks odtheking and odin contributors for this (they stole some of it as well);
// https://github.com/odtheking/Odin/blob/main/src/main/kotlin/me/odinmain/utils/skyblock/LocationUtils.kt;
// Odin is in kotlin, so at least I did something myself :peepoHappy:

import cc.polyfrost.oneconfig.events.event.ReceivePacketEvent;
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawInfo;
import cc.polyfrost.oneconfig.utils.hypixel.LocrawUtil;
import com.github.spookie6.frozen.events.impl.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.lang3.StringUtils;
import com.github.spookie6.frozen.events.impl.TablistUpdateEvent;
import com.github.spookie6.frozen.utils.skyblock.dungeon.Dungeon;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;

public class LocationUtils {
    public static boolean isOnHypixel = false;
    public static boolean isInSkyblock = false;
    public static Island currentArea = Island.Unknown;
    public static Dungeon currentDungeon = null;

    @SubscribeEvent
    public void onConnect(FMLNetworkEvent.ClientConnectedToServerEvent e) throws InterruptedException {
        if (Minecraft.getMinecraft().isSingleplayer()) {
            currentArea = Island.SingePlayer;
            return;
        }

        int i = 0;
        ServerData serverData = Minecraft.getMinecraft().getCurrentServerData();
        while (serverData == null && !isOnHypixel) {
            if (i == 20) break;
            wait(100);
            serverData = Minecraft.getMinecraft().getCurrentServerData();
            i++;
        }
        if (serverData == null) return;
        isOnHypixel = serverData.serverIP.contains("hypixel.net");
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPacket(PacketEvent.Received e) {
        if (LocationUtils.currentArea.isArea(Island.SingePlayer)) return;
        if (e.getPacket() instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload packet = (S3FPacketCustomPayload) e.getPacket();
            if (isOnHypixel || !packet.getChannelName().equals("MC|Brand")) return;
            if (packet.getBufferData().readStringFromBuffer((int) Short.MAX_VALUE).contains("hypixel")) isOnHypixel = true;
        }
        if (e.getPacket() instanceof S3BPacketScoreboardObjective) {
            LocrawInfo locrawInfo = LocrawUtil.INSTANCE.getLocrawInfo();
            if (locrawInfo == null) return;
            isInSkyblock = isOnHypixel && locrawInfo.getGameType().equals(LocrawInfo.GameType.SKYBLOCK);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTablistUpdate(TablistUpdateEvent e) {
        if (LocationUtils.currentArea.isArea(Island.SingePlayer)) return;
        if (!currentArea.isArea(Island.Unknown) || (!e.packet.getAction().equals(S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME) && !e.packet.getAction().equals(S38PacketPlayerListItem.Action.ADD_PLAYER))) return;
        S38PacketPlayerListItem.AddPlayerData area = e.packet.getEntries().stream()
                .filter(x -> StringUtils.startsWithAny(x.getDisplayName() == null ? "" : x.getDisplayName().getUnformattedText(), "Area: ", "Dungeon: "))
                .findFirst()
                .orElse(null);

        currentArea = area == null ? Island.Unknown : Island.findMatch(area.getDisplayName().getUnformattedText());
        if (DungeonUtils.getInDungeon() && currentDungeon == null) {
            currentDungeon = new Dungeon();
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        reset();
        isOnHypixel = false;
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {reset();}

    private void reset() {
        currentArea = Island.Unknown;
        isInSkyblock = false;
        currentDungeon = null;
    }
}
