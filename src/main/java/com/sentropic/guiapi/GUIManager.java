package com.sentropic.guiapi;

import com.sentropic.guiapi.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GUIManager implements Listener {
    private final Map<Player,GUI> GUIS = new HashMap<>();
    private final Map<Player,GUI> GUIS_READ = Collections.unmodifiableMap(GUIS);
    private final Task task = new Task();

    GUIManager() { task.runTaskTimer(GUIAPI.getPlugin(), 0, 1); }

    void disable() {
        try { task.cancel(); } catch (IllegalStateException ignored) { }
    }

    public GUI getGUI(Player player) {
        GUI gui = GUIS.get(player);
        if (gui == null) {
            gui = new GUI(player);
            GUIS.put(player, gui);
        }
        return gui;
    }

    public Map<Player,GUI> getGUIS() { return GUIS_READ; }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) { GUIS.remove(event.getPlayer()); }

    public class Task extends BukkitRunnable {
        @Override
        public void run() {
            GUIS.values().forEach(GUI::play);
        }
    }
}
