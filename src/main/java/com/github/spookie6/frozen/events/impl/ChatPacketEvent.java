package com.github.spookie6.frozen.events.impl;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ChatPacketEvent extends Event {
    public String original_message;
    public String message;
    public S02PacketChat packet;

    public ChatPacketEvent(String message, S02PacketChat packet) {
        this.original_message = packet.getChatComponent().getFormattedText();
        this.message = ChatFormatting.stripFormatting(message);
        this.packet = packet;
    }

    public void editMessage(String msg) {
        this.setCanceled(true);
        UChat.chat(msg);
    }
}
