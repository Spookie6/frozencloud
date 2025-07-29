package com.github.spookie6.frozen.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import com.github.spookie6.frozen.Frozen;

import static com.github.spookie6.frozen.Frozen.mc;

public class ChatUtils {
    public static void sendModInfo(String text) {
        UChat.chat(Frozen.chatPrefix + text);
    }

    public static void sendCommand(String command, boolean clientSide) {
        if (mc.isSingleplayer() && !clientSide) sendModInfo("Sending command " + command);
        if (clientSide) ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/" + command);
        else UChat.say("/"+command);
    }
}
