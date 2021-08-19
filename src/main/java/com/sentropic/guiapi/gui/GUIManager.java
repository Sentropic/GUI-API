package com.sentropic.guiapi.gui;

import com.sentropic.guiapi.GUIAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class in charge of storing, accessing and updating the {@link Player}s' {@link GUI}
 * To access the instance used by the plugin, use {@link GUIAPI#getGUIManager()}
 */
public class GUIManager implements Listener {
    private final Map<Player,GUI> GUIS = new HashMap<>();
    private final Map<Player,GUI> GUIS_READ = Collections.unmodifiableMap(GUIS);
    private final Task task = new Task();

    /**
     * For internal use only. Use {@link GUIAPI#getGUIManager()} to get the instance used by the plugin
     */
    public GUIManager() { task.runTaskTimer(GUIAPI.getPlugin(), 0, 1); }

    /**
     * For internal use only. Used to clear the stored {@link GUI}s from memory and cancel GUI updating
     */
    public void disable() {
        try {
            task.cancel();
            GUIS.clear();
        } catch (IllegalStateException ignored) { }
    }

    /**
     * Gets the {@link GUI} of a given {@link Player}
     * @param player the {@link Player} to get the {@link GUI} for
     * @return the existing {@link GUI} of the {@link Player}, or a new one if nonexistent
     */
    public GUI getGUI(Player player) {
        GUI gui = GUIS.get(player);
        if (gui == null) {
            gui = new GUI(player);
            GUIS.put(player, gui);
        }
        return gui;
    }

    /**
     * @return a read-only {@link Map} containing all {@link Player}s who have a {@link GUI},
     *      * and the respective GUIs
     */
    public Map<Player,GUI> getGUIS() { return GUIS_READ; }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) { GUIS.remove(event.getPlayer()); }

    private class Task extends BukkitRunnable {
        @Override
        public void run() {
            GUIS.values().forEach(GUI::play);
        }
    }
}
