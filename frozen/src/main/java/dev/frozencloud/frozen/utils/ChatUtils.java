package dev.frozencloud.frozen.utils;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import dev.frozencloud.core.ModEnum;
import net.minecraftforge.client.ClientCommandHandler;
import dev.frozencloud.frozen.Frozen;

import static dev.frozencloud.frozen.Frozen.mc;

public class ChatUtils {
    public static void sendModInfo(String text) {
        UChat.chat(ModEnum.FROZEN.getPrefix() + text);
    }

    public static void sendCommand(String command, boolean clientSide) {
        if (mc.isSingleplayer() && !clientSide) sendModInfo("Sending command " + command);
        if (clientSide) ClientCommandHandler.instance.executeCommand(mc.thePlayer, "/" + command);
        else UChat.say("/"+command);
    }
}
