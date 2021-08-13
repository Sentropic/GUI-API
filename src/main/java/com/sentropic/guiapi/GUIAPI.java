package com.sentropic.guiapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.sentropic.guiapi.command.ReloadCommand;
import com.sentropic.guiapi.listener.PacketListener;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class GUIAPI extends JavaPlugin {
    private static GUIAPI singleton;
    private static GUIManager guiManager;
    private static GUIConfig config;

    public static ProtocolManager protocolManager;
    private static PacketListener packetListener;

    @Override
    public void onEnable() {
        if (singleton != null) { throw new IllegalStateException(); }
        singleton = this;

        guiManager = new GUIManager();
        getServer().getPluginManager().registerEvents(guiManager, this);

        saveDefaultConfig();
        config = new GUIConfig();

        // TODO add config for character widths
        this.getCommand("guiapi").setExecutor(new ReloadCommand());

        protocolManager = ProtocolLibrary.getProtocolManager();
        packetListener = new PacketListener(this, PacketType.Play.Server.TITLE);
        protocolManager.addPacketListener(packetListener);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(guiManager);
        guiManager.disable();
        guiManager = null;
        config = null;

        protocolManager.removePacketListener(packetListener);
        packetListener = null;
        protocolManager = null;

        singleton = null;
    }

    public static GUIAPI getPlugin() { return singleton; }

    public static GUIManager getGUIManager() { return guiManager; }

    public static GUIConfig getGUIConfig() { return config; }
}
