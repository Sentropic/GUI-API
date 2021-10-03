package com.sentropic.guiapi;

import com.sentropic.guiapi.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

/**
 * Class in charge of storing, accessing and updating the {@link Player}s' {@link GUI}
 * To access the instance used by the plugin, use {@link GUIAPI#getGUIManager()}
 */
public class GUIManager implements Listener {
    private static final String METADATA_KEY = "guiapi:gui";

    final Set<GUI> GUIS = new HashSet<>();
    private final Task task = new Task();

    /**
     * For internal use only. Use {@link GUIAPI#getGUIManager()} to get the instance used by the plugin
     */
    public GUIManager() { task.runTaskTimer(GUIAPI.getPlugin(), 0, 1); }

    /**
     * For internal use only. Used to clear the stored {@link GUI}s from memory and cancel GUI updating
     */
    void disable() {
        try {
            task.cancel();
            for (GUI gui : GUIS) {
                gui.getPlayer().removeMetadata(METADATA_KEY, GUIAPI.getPlugin());
            }
            GUIS.clear();
        } catch (IllegalStateException ignored) { }
    }

    /**
     * Gets the {@link GUI} of a given {@link Player}
     *
     * @param player the {@link Player} to get the {@link GUI} for
     * @return the existing {@link GUI} of the {@link Player}, or a new one if nonexistent
     */
    public GUI getGUI(Player player) {
        GUI gui;
        if (player.hasMetadata(METADATA_KEY)) {
            gui = (GUI) player.getMetadata(METADATA_KEY).get(0).value();
        } else {
            gui = new GUI(player);
            GUIS.add(gui);
            player.setMetadata(METADATA_KEY, new FixedMetadataValue(GUIAPI.getPlugin(), gui));
        }
        return gui;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) { GUIS.remove(this.getGUI(event.getPlayer())); }

    private class Task extends BukkitRunnable {
        @Override
        public void run() {
            GUIS.forEach(GUI::play);
        }
    }
}
