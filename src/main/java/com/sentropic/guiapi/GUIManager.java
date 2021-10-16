package com.sentropic.guiapi;

import com.sentropic.guiapi.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    public GUIManager() {
        task.runTaskTimer(GUIAPI.getPlugin(), 0, 1);
        Bukkit.getServer().getOnlinePlayers().forEach(this::createGUI);
    }

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
     * @return the existing {@link GUI} of the {@link Player}
     * @throws IllegalStateException if the given player is offline
     */
    public GUI getGUI(Player player) {
        if (player.isOnline()) {
            return (GUI) player.getMetadata(METADATA_KEY).get(0).value();
        } else {
            throw new IllegalStateException();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GUIS.remove(this.getGUI(player));
        player.removeMetadata(METADATA_KEY, GUIAPI.getPlugin());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        createGUI(event.getPlayer());
    }

    private void createGUI(Player player) {
        GUI gui = new GUI(player);
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(GUIAPI.getPlugin(), gui));
        GUIS.add(gui);
    }

    private class Task extends BukkitRunnable {
        @Override
        public void run() {
            GUIS.forEach(GUI::play);
        }
    }
}
