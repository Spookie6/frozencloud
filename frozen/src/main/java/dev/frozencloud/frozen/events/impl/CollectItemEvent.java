package dev.frozencloud.frozen.events.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CollectItemEvent extends Event {
    public S0DPacketCollectItem packet;
    public Entity entity;

    public CollectItemEvent(S0DPacketCollectItem packet) {
        this.packet = packet;
        this.entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getCollectedItemEntityID());
    }
}
