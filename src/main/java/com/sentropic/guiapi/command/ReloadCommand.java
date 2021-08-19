package com.sentropic.guiapi.command;

import com.sentropic.guiapi.GUIAPI;
import com.sentropic.guiapi.gui.GUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            if (!sender.hasPermission("misc.command.reload")) { return false; }
            GUIAPI.getGUIConfig().reload();
            sender.sendMessage(ChatColor.GREEN+"[GUI API] Reloaded config.yml");
            return true;
        } else if (args.length == 1 && args[0].equals("debug")) {
            if (!sender.hasPermission("misc.command.debug")) { return false; }
            if (sender instanceof Player) {
                GUI gui = GUIAPI.getGUIManager().getGUI((Player) sender);
                gui.setDebug(!gui.isDebugging());
                return true;
            } else { sender.sendMessage("Command only runnable ingame"); }
        }
        return false;
    }
}
