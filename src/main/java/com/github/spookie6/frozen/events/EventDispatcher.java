package com.github.spookie6.frozen.events;

import com.github.spookie6.frozen.events.impl.*;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonEnums;
import com.github.spookie6.frozen.utils.skyblock.dungeon.DungeonUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventDispatcher {
    @SubscribeEvent
    public void onPacket(PacketEvent.Received e) {
        Packet<?> packet = e.getPacket();

        if (packet instanceof S32PacketConfirmTransaction) MinecraftForge.EVENT_BUS.post(new ServerTickEvent());
        if (packet instanceof S0DPacketCollectItem) MinecraftForge.EVENT_BUS.post(new CollectItemEvent((S0DPacketCollectItem) packet));
        if (packet instanceof S38PacketPlayerListItem) MinecraftForge.EVENT_BUS.post(new TablistUpdateEvent((S38PacketPlayerListItem) packet));
        if (packet instanceof S02PacketChat) MinecraftForge.EVENT_BUS.post(new ChatPacketEvent(((S02PacketChat) packet).getChatComponent().getUnformattedText(), (S02PacketChat) packet));

//        M7 Dragons
        if (packet instanceof S2APacketParticles) {
            S2APacketParticles particlePacket = (S2APacketParticles) packet;
            if (particlePacket.getParticleType().equals(EnumParticleTypes.ENCHANTMENT_TABLE) && DungeonUtils.getF7Phase().equals(DungeonEnums.M7Phases.P5)) {
                DungeonEnums.Dragon dragon = determineDragon(particlePacket);
                if (dragon == null) return;
                MinecraftForge.EVENT_BUS.post(new DragonSpawnEvent(particlePacket, dragon));
            }
        }
    }

    private DungeonEnums.Dragon determineDragon(S2APacketParticles packetParticles) {
        DungeonEnums.Dragon detectedDragon = null;

        double x = packetParticles.getXCoordinate();
        double y = packetParticles.getYCoordinate();
        double z = packetParticles.getZCoordinate();

        if (y >= 14 && y <= 19) {
            if (x >= 27 && x <= 32) {
                if (z == 59) {
                    detectedDragon = DungeonEnums.Dragon.RED;
                } else if (z == 94) {
                    detectedDragon = DungeonEnums.Dragon.GREEN;
                }
            } else if (x >= 79 && x <= 85) {
                if (z == 94) {
                    detectedDragon = DungeonEnums.Dragon.BLUE;
                } else if (z == 56) {
                    detectedDragon = DungeonEnums.Dragon.ORANGE;
                }
            } else if (x == 56) {
                detectedDragon = DungeonEnums.Dragon.PURPLE;
            }
        }
        return detectedDragon;
    }
}
