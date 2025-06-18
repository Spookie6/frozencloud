package com.github.spookie6.overlaymanager.commands;

import com.github.spookie6.overlaymanager.OverlayManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class MoveOverlays extends CommandBase {
    @Override
    public String getCommandName() { return "moveoverlays"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        OverlayManager.guiOverlayEditor.open();
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }
}
