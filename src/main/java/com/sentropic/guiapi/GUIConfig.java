package com.sentropic.guiapi;

import com.sentropic.guiapi.gui.Alignment;
import com.sentropic.guiapi.gui.Font;
import com.sentropic.guiapi.gui.GUI;
import com.sentropic.guiapi.gui.GUIComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GUIConfig {
    private int period;
    private final List<GUIComponent> debugComponents = new ArrayList<>();
    private final List<GUIComponent> debugComponentsRead = Collections.unmodifiableList(debugComponents);

    GUIConfig() { reload(); }

    public void reload() {
        GUIAPI plugin = GUIAPI.getPlugin();
        plugin.reloadConfig();
        FileConfiguration configFile = plugin.getConfig();
        period = configFile.getInt("send_period_milis", 1900);

        // Debug components
        {
            debugComponents.clear();
            ConfigurationSection debugSection = configFile.getConfigurationSection("debug");
            if (debugSection != null) {
                for (String componentKey : debugSection.getKeys(false)) {
                    try {
                        ConfigurationSection componentSection = Objects.requireNonNull(debugSection.getConfigurationSection(componentKey));
                        ConfigurationSection fontSection = Objects.requireNonNull(componentSection.getConfigurationSection("font"));

                        String id = GUI.ID_DEBUG+componentKey;
                        int offset = componentSection.getInt("offset");
                        String text = Objects.requireNonNull(componentSection.getString("text"));
                        int width = componentSection.getInt("width", -1);

                        String fontID = Objects.requireNonNull(fontSection.getString("id"));
                        Font font = Font.getRegistered(fontID);
                        if (font == null) { font = new Font(fontID, fontSection.getInt("height", 8)); }
                        Alignment alignment = Alignment.valueOf(
                                Objects.requireNonNull(componentSection.getString("alignment")).toUpperCase());
                        boolean scale = componentSection.getBoolean("scale", true);

                        GUIComponent component;
                        if (width == -1) {
                            component = new GUIComponent(id, offset, text, font, alignment, scale);
                        } else {
                            component = new GUIComponent(id, offset, text, width, font, alignment);
                        }
                        debugComponents.add(component);
                    } catch (NullPointerException | IllegalArgumentException ignored) { }
                }
            }
            // Update
            for (GUI gui : GUIAPI.getGUIManager().getGUIS().values()) {
                if (gui.isDebugging()) {
                    gui.setDebug(false);
                    gui.setDebug(true);
                }
            }
        }
    }

    public int getSendPeriod() { return period; }

    public List<GUIComponent> getDebugComponents() { return debugComponentsRead; }
}
