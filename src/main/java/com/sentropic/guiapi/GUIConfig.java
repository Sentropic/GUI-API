package com.sentropic.guiapi;

import com.sentropic.guiapi.gui.Alignment;
import com.sentropic.guiapi.gui.Font;
import com.sentropic.guiapi.gui.GUI;
import com.sentropic.guiapi.gui.GUIComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the config options of the plugin
 * For efficiency, use instead of directly getting the config file from the plugin
 */
public class GUIConfig {
    private int sendPeriod;
    private int anonPeriod;
    private int anonDuration;
    private final List<GUIComponent> debugComponents = new ArrayList<>();
    private final List<GUIComponent> debugComponentsRead = Collections.unmodifiableList(debugComponents);

    GUIConfig() { reload(); }

    /**
     * Reloads the plugin's config and stores all its values
     */
    public void reload() {
        GUIAPI plugin = GUIAPI.getPlugin();
        plugin.reloadConfig();
        FileConfiguration configFile = plugin.getConfig();
        sendPeriod = configFile.getInt("send_period_milis", 1900);
        anonPeriod = configFile.getInt("anon_period_ticks", 20);
        anonDuration = configFile.getInt("anon_duration_ticks", 40);

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
                        int offset = componentSection.getInt("offset", 0);
                        String text = Objects.requireNonNull(componentSection.getString("text"));
                        int width = componentSection.getInt("width", -1);

                        String fontID = Objects.requireNonNull(fontSection.getString("id"));
                        Font font = Font.getRegistered(fontID);
                        if (font == null) { font = new Font(fontID, fontSection.getInt("height", 8)); }
                        Alignment alignment = Alignment.valueOf(
                                Objects.requireNonNull(componentSection.getString("alignment")).toUpperCase());
                        boolean scale = componentSection.getBoolean("scale", true);

                        TextComponent textComponent = GUIComponent.createTextComponent(text, font.getID());
                        GUIComponent component;
                        if (width == -1) {
                            component = new GUIComponent(id, textComponent, font.getWidth(text, scale), offset, alignment);
                        } else {
                            component = new GUIComponent(id, textComponent, width, offset, alignment);
                        }
                        debugComponents.add(component);
                    } catch (NullPointerException | IllegalArgumentException ignored) { }
                }
            }
            GUIAPI.getGUIManager().GUIS.forEach(GUI::onReload);
        }
    }

    /**
     * @return the maximum delay between sending GUIs to players, in milisecons
     */
    public int getSendPeriod() { return sendPeriod; }

    /**
     * Gets the amount of ticks that each anonymous action bar text (sent without the usage of GUIAPI)
     * is shown in the GUI before switching to a different one, if more than one is available
     *
     * @return the period at which anonymous action bar texts are cycled around in the GUI
     */
    public int getAnonPeriod() { return anonPeriod; }

    /**
     * Gets the amount of ticks that each anonymous action bar text (sent without the usage of GUIAPI)
     * remains in memory before being removed (defaults to 40 in vanilla Minecraft)
     *
     * @return the duration of anonymous action bars
     */
    public int getAnonDuration() { return anonDuration; }

    /**
     * Gets the debug {@link GUIComponent}s defined in the config
     *
     * @return a {@link List} containing the debug {@link GUIComponent}s defined in the config, in typing order
     */
    public List<GUIComponent> getDebugComponents() { return debugComponentsRead; }
}
