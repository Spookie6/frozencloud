package dev.frozencloud.frozen.events.impl;

import dev.frozencloud.frozen.utils.skyblock.dungeon.DungeonEnums;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DragonSpawnEvent extends Event {
    public DungeonEnums.Dragon dragon;
    public S2APacketParticles packet;

    public DragonSpawnEvent (S2APacketParticles packet, DungeonEnums.Dragon dragon) {
        this.packet = packet;
        this.dragon = dragon;
    }
}
