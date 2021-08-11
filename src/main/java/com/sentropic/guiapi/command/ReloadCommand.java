package com.sentropic.guiapi.command;

import com.sentropic.guiapi.GUIAPI;
import com.sentropic.guiapi.GUIManager;
import com.sentropic.guiapi.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("misc.command.reload")) { return false; }
        if (args.length == 1 && args[0].equals("reload")) {
            GUIAPI.getPlugin().reloadConfig();
            sender.sendMessage(ChatColor.GREEN+"[GUIAPI] Reloaded config.yml");
            return true;
        } else if (args.length == 1 && args[0].equals("debug")) {
            if (sender instanceof Player) {
                GUI gui = GUIAPI.getGUIManager().getGUI((Player) sender);
                gui.setDebug(!gui.isDebugging());
                return true;
            } else { sender.sendMessage("Command only runnable ingame"); }
        }
        return false;
    }
}
