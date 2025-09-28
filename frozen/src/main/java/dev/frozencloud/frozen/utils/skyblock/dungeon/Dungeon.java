package dev.frozencloud.frozen.utils.skyblock.dungeon;

import dev.frozencloud.frozen.config.ModConfig;
import dev.frozencloud.frozen.events.impl.PacketEvent;
import dev.frozencloud.frozen.utils.ChatUtils;
import dev.frozencloud.frozen.utils.skyblock.ScoreboardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.frozencloud.frozen.Frozen.mc;

public class Dungeon {
    public boolean inBoss = false;
    public DungeonEnums.Floor floor = DungeonEnums.Floor.None;

    public static final Pattern FLOOR_PATTERN = Pattern.compile(".*The Catacombs \\(([EMF1-7]+)\\)$");

    List<DungeonEnums.DungeonPlayer> dungeonPlayers = new ArrayList<>();

    public DungeonEnums.DungeonPlayer getCurrentDungeonPlayer() {
        return(dungeonPlayers.stream()
                .filter(x -> x.username.equals(Minecraft.getMinecraft().thePlayer.getDisplayNameString()))
                .findFirst()
                .orElse(null));
    }

    public List<DungeonEnums.DungeonPlayer> getDungeonPlayers () {
        return this.dungeonPlayers;
    }

    public void onPacket(PacketEvent.Received e) {
        if (e.getPacket() instanceof S38PacketPlayerListItem) {
            S38PacketPlayerListItem packet = (S38PacketPlayerListItem) e.getPacket();
            if (!packet.getAction().equals(S38PacketPlayerListItem.Action.ADD_PLAYER) && !packet.getAction().equals(S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME)) return;
            List<String> tablistEntries = packet.getEntries().stream().map(x -> x.getDisplayName() == null ? "" : x.getDisplayName().getUnformattedText()).collect(Collectors.toList());
            this.updatePlayers(tablistEntries);
        }
    }

    public void onTick() {
        if (mc.theWorld == null || mc.thePlayer == null || !this.floor.equals(DungeonEnums.Floor.None)) return;

        List<String> scoreboardLines = ScoreboardUtils.getScoreboardLines();

        for (String line : scoreboardLines) {
            Matcher matcher = FLOOR_PATTERN.matcher(line);
            if (matcher.find()) {
                this.floor = DungeonEnums.Floor.getFloor(matcher.group(1));
                SplitsManager.initialize(this.floor);
                if (ModConfig.autoPotBag) ChatUtils.sendCommand("pb", false);
                break;
            }
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

