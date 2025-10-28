package dev.frozencloud.frozen.mixin;

import dev.frozencloud.frozen.events.impl.PacketEvent;
import dev.frozencloud.frozen.events.impl.TitleEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class, priority = 1001)
public abstract class MixinNetworkManager extends SimpleChannelInboundHandler<Packet<?>> {
    @Inject(method = "sendPacket*", at = @At("HEAD"))
    private void frozen$onSendPacket(Packet<?> packet, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new PacketEvent.Send(packet));
    }

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    private void frozen$onReceivePacket(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        PacketEvent.Received event = new PacketEvent.Received(packet);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }

        if (packet instanceof S45PacketTitle) {
            S45PacketTitle titlePacket = (S45PacketTitle) packet;
            S45PacketTitle.Type type = titlePacket.getType();
            IChatComponent component = titlePacket.getMessage();

            if (component != null) {
                TitleEvent.Incoming titleEvent = new TitleEvent.Incoming(type, component);
                MinecraftForge.EVENT_BUS.post(titleEvent);

                if (titleEvent.isCanceled()) {
                    ci.cancel();
                }
            }
        }
    }
}
