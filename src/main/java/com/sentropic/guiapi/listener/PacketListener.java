package com.sentropic.guiapi.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.sentropic.guiapi.gui.GUI;
import org.bukkit.plugin.Plugin;

public class PacketListener extends PacketAdapter {
    public PacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packetContainer = event.getPacket();
        if (!packetContainer.getType().equals(PacketType.Play.Server.TITLE) || GUI.isSendingPacket()) {
            return;
        }
        //TODO see if can order anonymous packets. Otherwise, add config option to ban them.
    }
}
