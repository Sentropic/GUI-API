package com.sentropic.guiapi;

import com.sentropic.guiapi.gui.GUI;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class GUIManager implements Listener {
    private final Map<Player,GUI> GUIS = new HashMap<>();
    private final Task task = new Task();

    //TODO test bigger periods
    GUIManager() { task.runTaskTimer(GUIAPI.getPlugin(), 0, 1); }

    void close() {
        try { task.cancel(); }
        catch (IllegalStateException ignored) { }
    }

    public GUI getGUI(Player player) {
        GUI gui = GUIS.get(player);
        if (gui == null) {
            gui = new GUI(player);
            GUIS.put(player, gui);
        }
        return gui;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) { GUIS.remove(event.getPlayer()); }

    public class Task extends BukkitRunnable {
        @Override
        public void run() {
            GUIS.values().stream()
                .filter(gui -> !gui.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                .forEach(GUI::play);
        }
    }
}
