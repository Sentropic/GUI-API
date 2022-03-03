package com.sentropic.guiapi;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.utility.MinecraftVersion;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.sentropic.guiapi.gui.GUI;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.plugin.Plugin;

public class PacketManager {
    private final ProtocolManager protocolManager;
    private final PacketAdapter packetAdapter;

    PacketManager(Plugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        if (MinecraftVersion.atOrAbove(new MinecraftVersion("1.17"))) {
            packetAdapter = new Manager_1_17(plugin);
        } else {
            packetAdapter = new Manager_1_16(plugin);
        }
        protocolManager.addPacketListener(packetAdapter);
    }

    public void disable() { protocolManager.removePacketListener(packetAdapter); }

    private static class Manager_1_17 extends PacketAdapter {
        public Manager_1_17(Plugin plugin) {
            super(plugin, PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event.isCancelled() || GUI.isSending()) { return; }
            boolean success = GUIAPI.getGUIManager().getGUI(event.getPlayer()).addAnonComponent(
                    ComponentSerializer.parse(event.getPacket().getChatComponents().read(0).getJson())[0]);
            event.setCancelled(success);
        }
    }

    private static class Manager_1_16 extends PacketAdapter {
        public Manager_1_16(Plugin plugin) {
            super(plugin, PacketType.Play.Server.TITLE);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (event.isCancelled() || GUI.isSending()) { return; }
            PacketContainer packet = event.getPacket();
            if (!packet.getTitleActions().read(0).equals(EnumWrappers.TitleAction.ACTIONBAR)) { return; }
            boolean success = GUIAPI.getGUIManager().getGUI(event.getPlayer()).addAnonComponent(
                    ComponentSerializer.parse(packet.getChatComponents().read(0).getJson())[0]);
            event.setCancelled(success);
        }
    }
}
