package com.github.spookie6.frozen.events.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ChatPacketEvent extends Event {
    public String message;
    public S02PacketChat packet;

    public ChatPacketEvent(String message, S02PacketChat packet) {
        this.message = ChatFormatting.stripFormatting(message);
        this.packet = packet;
    }
}
