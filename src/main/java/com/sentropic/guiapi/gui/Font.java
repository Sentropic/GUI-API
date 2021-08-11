package com.sentropic.guiapi.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Font {

    // Static code

    public static final Font DEFAULT = new Font("minecraft:default", 8);
    private static final Map<String,Font> registeredFonts = new HashMap<>();

    static {
        DEFAULT.registerWidth('I', 4);
        DEFAULT.registerWidth('f', 5);
        DEFAULT.registerWidth('i', 2);
        DEFAULT.registerWidth('k', 5);
        DEFAULT.registerWidth('l', 3);
        DEFAULT.registerWidth('t', 4);
        DEFAULT.registerWidth(' ', 4);
        DEFAULT.registerWidth('.', 2);

        register(DEFAULT);
    }

    @Nullable
    public static Font ofName(String name) { return registeredFonts.get(name); }

    public static void register(Font font) {
        String name = font.getID();
        if (registeredFonts.containsKey(name)) {
            throw new IllegalArgumentException("Font \""+name+"\" already exists");
        }
        registeredFonts.put(name, font);
    }

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
        if (height < 5) { height += 1; } // Correction necessary for some reason
        this.height = height;
    }

    public void registerWidth(char character, int width) {
        if (widths == null) { widths = new HashMap<>(); }
        widths.put(character, width);
    }

    public int getWidth(char character, boolean scale) {
        int result;
        if (this == DEFAULT) {
            result = widths.getOrDefault(character, 6);
        } else {
            result = widths == null ?
                    DEFAULT.getWidth(character, scale) :
                    widths.getOrDefault(character, DEFAULT.getWidth(character, scale));
        }
        if (scale) { result = (int) Math.ceil(result*height/8f); }
        return result;
    }

    public int getWidth(String text, boolean scale) {
        if (text.equals("")) { return 0; }
        int total = 0;
        for (char character : text.toCharArray()) { total += getWidth(character, scale); }
        return total;
    }

    @Override
    public String toString() { return id; }

    public String getID() { return id; }

    public int getHeight() { return height; }
}
