package com.github.spookie6.frozen.utils.skyblock.dungeon;

import com.github.spookie6.frozen.config.ModConfig;
import com.github.spookie6.frozen.events.impl.PacketEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Dungeon {
    public boolean inBoss = false;
    public DungeonEnums.Floor floor = DungeonEnums.Floor.None;

    List<DungeonEnums.DungeonPlayer> dungeonPlayers = new ArrayList<>();

    public DungeonEnums.DungeonPlayer getCurrentDungeonPlayer() {
        return(dungeonPlayers.stream()
                .filter(x -> x.username.equals(Minecraft.getMinecraft().thePlayer.getDisplayNameString()))
                .findFirst()
                .orElse(null));
    }

    public void onPacket(PacketEvent.Received e) {
        if (e.getPacket() instanceof S3EPacketTeams) {
            if (!this.floor.equals(DungeonEnums.Floor.None)) return;
            S3EPacketTeams packet = (S3EPacketTeams) e.getPacket();
            if (packet.getAction() != 2 || this.floor != DungeonEnums.Floor.None) return;
            String txt = ChatFormatting.stripFormatting(packet.getPrefix() + packet.getSuffix());
            if (txt == null || txt.isEmpty()) return;

            Pattern pattern = Pattern.compile(".*The Catacombs \\(([EMF1-7]+)\\)$");
            Matcher matcher = pattern.matcher(txt);

            if (!matcher.find()) return;
            this.floor = DungeonEnums.Floor.getFloor(matcher.group(1));
            if (ModConfig.debugMessages) ChatUtils.sendModInfo("Joined floor: " + this.floor.toString());
            SplitsManager.initialize(this.floor);
        }
        if (e.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) e.getPacket();
            if (!packet.getAction().equals(S38PacketPlayerListItem.Action.ADD_PLAYER) && !packet.getAction().equals(S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME)) return;
            List<String> tablistEntries = packet.getEntries().stream().map(x -> x.getDisplayName() == null ? "" : x.getDisplayName().getUnformattedText()).collect(Collectors.toList());
            this.updatePlayers(tablistEntries);
        }
    }

    public void updatePlayers(List<String> tablist) {
        dungeonPlayers = DungeonUtils.getDungeonPlayers(dungeonPlayers, tablist);
    }

    public void onEntityJoin(EntityJoinWorldEvent e) {
        DungeonEnums.DungeonPlayer teammate = this.dungeonPlayers.stream()
                .filter(x -> x.entity.getName().equals(x.entity.getName()))
                .findFirst()
                .orElse(null);

        if (teammate == null) return;
        if (e.entity instanceof EntityPlayer) teammate.entity = (EntityPlayer) e.entity;
    }
}

