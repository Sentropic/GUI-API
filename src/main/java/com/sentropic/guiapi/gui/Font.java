package com.sentropic.guiapi.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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

    @Nullable
    public static Font getRegistered(String id) { return registeredFonts.get(id); }

    public static void register(Font font) {
        String id = font.getID();
        if (registeredFonts.containsKey(id)) {
            throw new IllegalArgumentException("Font \""+id+"\" is already registered");
        }
        registeredFonts.put(id, font);
    }

    @SuppressWarnings("unused")
    public static boolean unregister(Font font) {
        boolean success = false;
        if (registeredFonts != null) { success = registeredFonts.remove(font.toString()) != null; }
        return success;
    }

    // Instance code

    private final String id;
    private final int height;
    private Map<Character,Integer> widths;

    public Font(@NotNull String id, int height) {
        this.id = id;
        this.height = height;
    }

    public void registerWidth(char character, int width) {
        if (widths == null) { widths = new HashMap<>(); }
        widths.put(character, width);
    }

    public int getWidth(char character, boolean scale) {
        Integer result = null;
        if (this == DEFAULT) { result = widths.getOrDefault(character, 6); } else {
            try { result = widths.get(character); } catch (NullPointerException ignored) { }
            if (result == null) { result = DEFAULT.getWidth(character, false); }
        }
        if (scale && this != DEFAULT && character != ' ') {
            // Formula figured out experimentally (pain)
            result = (int) Math.round(1.1249999d+(result-1)*height/8d);
        }
        return result;
    }

    public int getWidth(String text, boolean scale) {
        int total = 0;
        for (Character character : text.toCharArray()) { total += getWidth(character, scale); }
        return total;
    }

    @Override
    public String toString() { return id; }

    public String getID() { return id; }

    @SuppressWarnings("unused")
    public int getHeight() { return height; }
}
