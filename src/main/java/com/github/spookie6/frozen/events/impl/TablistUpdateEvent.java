package com.github.spookie6.frozen.events.impl;

import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TablistUpdateEvent extends Event {
    public S38PacketPlayerListItem packet;

    public TablistUpdateEvent(S38PacketPlayerListItem packet) {
        this.packet = packet;
    }

    public S38PacketPlayerListItem.AddPlayerData findFirstLine(String regex) {
        return(this.packet.getEntries().stream()
                .filter(x -> x.getDisplayName().getUnformattedText().matches(regex))
                .findFirst()
                .orElse(null));
    }
}
