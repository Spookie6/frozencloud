package com.github.spookie6.frozen.commands;

import com.github.spookie6.frozen.Frozen;
import com.github.spookie6.frozen.utils.ChatUtils;
import com.github.spookie6.frozen.utils.skyblock.LocationUtils;
import com.github.spookie6.frozen.utils.skyblock.dungeon.SplitsManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

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
        add(helpCmdClickable("splits", "Returns current dungeon's splits. (debug)"));
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
            case "splits":
                String txt = SplitsManager.getText();
                if (txt.isEmpty()) ChatUtils.sendModInfo("No splits");
                else {
                    for (String line : txt.split("\n")) {
                        ChatUtils.sendModInfo(line);
                    }
                }
                break;
            case "scan":
                new DebugShurikenScanner().scanForShurikenEntities();
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

    public class DebugShurikenScanner {

        public void scanForShurikenEntities() {

            if (mc.theWorld == null) return;

            for (Entity entity : mc.theWorld.loadedEntityList) {
                String raw = null;

                // Try custom name tag first (often used on Hypixel)
                if (entity.hasCustomName()) {
                    raw = entity.getCustomNameTag();
                }

                // Fallback to display name (for other cases)
                if ((raw == null || raw.isEmpty()) && entity instanceof EntityLivingBase) {
                    IChatComponent comp = ((EntityLivingBase) entity).getDisplayName();
                    raw = comp.getUnformattedText();
                }

                if (raw != null && raw.endsWith("✯")) {
                    System.out.println("Found Shuriken mob:");
                    System.out.println("  Type: " + entity.getClass().getName());
                    System.out.println("  Name: " + raw);
                    System.out.println("  Pos:  " + entity.posX + ", " + entity.posY + ", " + entity.posZ);
                }
            }
        }
    }
}
