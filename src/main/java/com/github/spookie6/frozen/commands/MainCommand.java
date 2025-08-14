package com.github.spookie6.frozen.commands;

import cc.polyfrost.oneconfig.libs.universal.UChat;
import com.github.spookie6.frozen.Frozen;
import com.github.spookie6.frozen.events.impl.ChatPacketEvent;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.spookie6.frozen.Frozen.mc;

public class MainCommand extends CommandBase {

    private static final List<IChatComponent> helpMsgLines = new ArrayList<IChatComponent>() {{
        add(new ChatComponentText(" "));
        add(new ChatComponentText("§7―――――――――§f[§bFrozen Help Desk§f]§7―――――――――§r"));
        add(new ChatComponentText("§o§8Version: §3§l§o" + Frozen.VERSION + "§r"));
        add(new ChatComponentText("§l§8Commands list§r"));
        add(helpCmdClickable("help", "Sends this message."));
        add(helpCmdClickable("moveoverlays", "Opens the overlay editor gui."));
        add(helpCmdClickable("location", "Returns your current location. (debug)"));
        add(helpCmdClickable("chatsim", "Simulates a chat message (debug)"));
        add(new ChatComponentText(" "));
    }};

    private static IChatComponent helpCmdClickable(String cmdName, String description) {
        ChatComponentText baseComponent = new ChatComponentText("§f[§b/fr " + cmdName + "§f]§r");
        ChatComponentText descriptionComponent = new ChatComponentText(" §7» " + description);

        baseComponent.setChatStyle(new ChatStyle()
                .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fr " + cmdName)));

        baseComponent.appendSibling(descriptionComponent);
        return baseComponent;
    }

    @Override
    public String getCommandName() { return "frozen"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            Frozen.config.openGui();
            return;
        }

        String subcmd = args[0].toLowerCase();
        List<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.remove(0);

        switch (subcmd) {
            case "help":
                for (IChatComponent chatComponent : helpMsgLines) {
                    mc.thePlayer.addChatComponentMessage(chatComponent);
                }
                break;
            case "location":
            case "loc":
                ChatUtils.sendModInfo(LocationUtils.isInSkyblock ? "In Skyblock" : "Not in Skyblock");
                ChatUtils.sendModInfo(LocationUtils.currentArea.toString());
                break;
            case "moveoverlays":
            case "mo":
                Frozen.guiOverlayEditor.open();
                break;
            case "chatsim": {
                String stringToSim = String.join(" ", arguments);
                UChat.chat(stringToSim);
                try {
                    MinecraftForge.EVENT_BUS.post(new ChatPacketEvent(stringToSim, new S02PacketChat(new ChatComponentText(stringToSim))));
                } catch(NullPointerException ignored) {;}
                break;
            }
            default:
                ChatUtils.sendModInfo("Couldn't find the command you are looking for.");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Command for frozen mod, /frozen help for more info!";
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("fr");
    }
}
