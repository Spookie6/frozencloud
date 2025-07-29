package com.github.spookie6.frozen.mixin;

import com.github.spookie6.frozen.events.impl.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class, priority = 1001)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet<?>> {
    @Inject(method = "sendPacket*", at = @At("HEAD"))
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new PacketEvent.Send(packet));
    }

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new PacketEvent.Received(packet));

        if (packet instanceof S32PacketConfirmTransaction) MinecraftForge.EVENT_BUS.post(new ServerTickEvent());
        if (packet instanceof S0DPacketCollectItem) MinecraftForge.EVENT_BUS.post(new CollectItemEvent((S0DPacketCollectItem) packet));
        if (packet instanceof S38PacketPlayerListItem) MinecraftForge.EVENT_BUS.post(new TablistUpdateEvent((S38PacketPlayerListItem) packet));
        if (packet instanceof S02PacketChat) MinecraftForge.EVENT_BUS.post(new ChatPacketEvent(((S02PacketChat) packet).getChatComponent().getUnformattedText(), (S02PacketChat) packet));
    }
}
