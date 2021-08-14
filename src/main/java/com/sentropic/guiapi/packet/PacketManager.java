package com.sentropic.guiapi.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.sentropic.guiapi.GUIAPI;
import com.sentropic.guiapi.gui.GUI;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.plugin.Plugin;

public class PacketManager extends PacketAdapter {
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public PacketManager() {
        this(GUIAPI.getPlugin(), PacketType.Play.Server.TITLE);
    }

    private PacketManager(Plugin plugin, PacketType... types) {
        super(plugin, types);
        protocolManager.addPacketListener(this);
    }

    public void disable() { protocolManager.removePacketListener(this); }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.isCancelled()) { return; }
        PacketContainer packetContainer = event.getPacket();
        if (!GUI.isSending()) {
            WrapperPlayServerTitle packet = new WrapperPlayServerTitle(packetContainer);
            if (packet.getAction().equals(EnumWrappers.TitleAction.ACTIONBAR) && !GUI.isSending()) {
                boolean success = GUIAPI.getGUIManager().getGUI(event.getPlayer()).addAnonComponent(
                        ComponentSerializer.parse(packet.getTitle().getJson())[0]);
                event.setCancelled(success);
            }
        }
    }
}
