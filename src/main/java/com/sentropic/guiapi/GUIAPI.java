package com.sentropic.guiapi;

import com.sentropic.guiapi.command.ReloadCommand;
import com.sentropic.guiapi.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;

public final class GUIAPI extends JavaPlugin implements Listener {
    private static GUIAPI singleton;
    private static GUIManager guiManager;
    private static GUIConfig config;
    private static PacketManager packetManager = null;

    @Override
    public void onEnable() {
        if (singleton != null) { throw new IllegalStateException(); }
        singleton = this;
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(this, this);

        guiManager = new GUIManager();
        pluginManager.registerEvents(guiManager, this);

        saveDefaultConfig();
        config = new GUIConfig();

        // TODO add config for character widths
        Objects.requireNonNull(this.getCommand("guiapi")).setExecutor(new ReloadCommand());

        if (Arrays.stream(pluginManager.getPlugins()).anyMatch(
                plugin -> plugin.getName().equals("ProtocolLib") && plugin.isEnabled())) {
            setProtocolLib(true);
        }

    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(guiManager);
        guiManager.disable();
        guiManager = null;
        config = null;

        setProtocolLib(false);

        HandlerList.unregisterAll((Listener) this);
        singleton = null;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin().getName().equals("ProtocolLib")) {
            setProtocolLib(true);
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals("ProtocolLib")) {
            setProtocolLib(false);
        }
    }

    private void setProtocolLib(boolean enabled) {
        if ((packetManager != null) == enabled) { return; }
        if (enabled) {
            packetManager = new PacketManager(this);
        } else {
            packetManager.disable();
            packetManager = null;
        }
    }

    /**
     * Gets the singleton {@link GUIAPI} object loaded by the server
     *
     * @return the singleton {@link GUIAPI} object
     */
    public static GUIAPI getPlugin() { return singleton; }

    /**
     * Gets the singleton {@link GUIManager} loaded by the plugin. Use this to access a {@link Player}s' {@link GUI}
     *
     * @return the {@link GUIManager} instance loaded by the plugin
     */
    public static GUIManager getGUIManager() { return guiManager; }

    /**
     * Gets the singleton {@link GUIConfig} loaded by the plugin
     *
     * @return the {@link GUIConfig} instance loaded by the plugin
     */
    public static GUIConfig getGUIConfig() { return config; }
}
