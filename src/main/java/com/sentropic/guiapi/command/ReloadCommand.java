package com.sentropic.guiapi.command;

import com.sentropic.guiapi.GUIAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            GUIAPI.getPlugin().reloadConfig();
            sender.sendMessage(ChatColor.GREEN+"[GUIAPI] Reloaded config.yml");
            return true;
        }
        return false;
    }
}
