package com.sentropic.guiapi.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a resource pack font, containing its ID and character widths
 */
public class Font {

    // Static code

    public static final Font DEFAULT = new Font("minecraft:default", 8);
    private static final Map<String,Font> registeredFonts = new HashMap<>();

    static {
        // Register default char widths (width = horizontal pixels + 1)
        DEFAULT.registerWidth(' ', 4);
        DEFAULT.registerWidth('f', 5);
        DEFAULT.registerWidth('i', 2);
        DEFAULT.registerWidth('k', 5);
        DEFAULT.registerWidth('l', 3);
        DEFAULT.registerWidth('t', 4);
        DEFAULT.registerWidth('I', 4);
        DEFAULT.registerWidth('í', 3);
        DEFAULT.registerWidth('Í', 4);
        DEFAULT.registerWidth('´', 3);
        DEFAULT.registerWidth('.', 2);
        DEFAULT.registerWidth(',', 2);
        DEFAULT.registerWidth(';', 2);
        DEFAULT.registerWidth(':', 2);
        DEFAULT.registerWidth('[', 4);
        DEFAULT.registerWidth(']', 4);
        DEFAULT.registerWidth('{', 4);
        DEFAULT.registerWidth('}', 4);
        DEFAULT.registerWidth('*', 4);
        DEFAULT.registerWidth('!', 2);
        DEFAULT.registerWidth('¡', 2);
        DEFAULT.registerWidth('"', 4);
        DEFAULT.registerWidth('(', 4);
        DEFAULT.registerWidth(')', 4);
        DEFAULT.registerWidth('°', 5);
        DEFAULT.registerWidth('|', 2);
        DEFAULT.registerWidth('`', 3);
        DEFAULT.registerWidth('\'', 2);
        DEFAULT.registerWidth('<', 5);
        DEFAULT.registerWidth('>', 5);
        DEFAULT.registerWidth('@', 7);
        DEFAULT.registerWidth('~', 7);

        register(DEFAULT);
    }

    /**
     * Gets a registered font by its ID
     * @param id the id of the font
     * @return the font for the given ID, or null if was not found
     */
    @Nullable
    public static Font getRegistered(String id) { return registeredFonts.get(id); }

    /**
     * Registers a Font to be accessed statically later, through {@link Font#getRegistered(String)}
     * @param font the Font to be registered
     * @throws IllegalArgumentException if a font with the same ID already is registered
     */
    public static void register(Font font) {
        String id = font.getID();
        if (registeredFonts.containsKey(id)) {
            throw new IllegalArgumentException("Font \""+id+"\" is already registered");
        }
        registeredFonts.put(id, font);
    }

    /**
     * Unregisters a Font from the static context
     * @param font the font to unregister
     * @return true if the font was unregistered, false if it was not previously registered
     */
    @SuppressWarnings("unused")
    public static boolean unregister(Font font) {
        boolean success = false;
        if (registeredFonts != null) { success = registeredFonts.remove(font.toString()) != null; }
        return success;
    }

    // Instance code

    private final String id;
    private final int height;
    private Font parent;
    private Map<Character,Integer> widths;

    /**
     * @param id the namespaced ID of the font, as used by the resource pack (i.e. "minecraft:default")
     * @param height the default height of the characters in the font, as specified in the resource pack
     */
    public Font(@NotNull String id, int height) {
        this.id = id;
        this.height = height;
    }

    /**
     * Creates a font that inherits its character widths from a parent font
     * Used for fonts that share the same textures with another one
     * @param id the namespaced ID of the font, as used by the resource pack (i.e. "minecraft:default")
     * @param height the default height of the characters in the font, as specified in the resource pack
     * @param parent the font to inherit its character widths from
     */
    public Font(@NotNull String id, int height, Font parent) {
        this.id = id;
        this.height = height;
        this.parent = parent;
    }

    /**
     * Registers the width of a character for this font, if different from the default of 6
     * @param character the character to register the width for
     * @param width the width of the character
     */
    public void registerWidth(char character, int width) {
        if (widths == null) { widths = new HashMap<>(); }
        widths.put(character, width);
    }

    /**
     * Gets the width of a given character for this font
     * @param character the character to get the width for
     * @param scale whether to scale the width according to the font's height
     * @return the width of the character
     */
    public int getWidth(char character, boolean scale) {
        Integer result = null;
        if (this == DEFAULT) { result = widths.getOrDefault(character, 6); } else {
            try { result = widths.get(character); } catch (NullPointerException ignored) { }
            if (result == null) {
                result = parent == null ?
                        DEFAULT.getWidth(character, false) :
                        parent.getWidth(character, false);
            }
        }
        if (scale && this != DEFAULT && character != ' ') {
            // Formula figured out experimentally (pain)
            result = (int) Math.round(1.1249999d+(result-1)*height/8d);
        }
        return result;
    }

    /**
     * Calculates the width of a given {@link String} for this font
     * @param text the String to calculate the width for
     * @param scale whether to scale the width according to the font's height
     * @return the calculated width of the character
     */
    public int getWidth(String text, boolean scale) {
        int total = 0;
        for (Character character : text.toCharArray()) { total += getWidth(character, scale); }
        return total;
    }

    /**
     * @return the namespaced ID of the font, as used by the resource pack (i.e. "minecraft:default")
     */
    @Override
    public String toString() { return id; }

    /**
     * @return the namespaced ID of the font, as used by the resource pack (i.e. "minecraft:default")
     */
    public String getID() { return id; }

    /**
     * @return the default height of the characters in the font, as specified in the resource pack
     */
    @SuppressWarnings("unused")
    public int getHeight() { return height; }
}
