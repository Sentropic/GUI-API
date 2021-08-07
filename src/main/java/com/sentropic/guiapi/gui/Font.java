package com.sentropic.guiapi.gui;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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
        String name = font.getName();
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

    private final String name;
    private final int height;
    private Map<Character,Integer> widths;

    public Font(String name, int height) {
        this.name = name;
        if (height < 5) { height += 1; } // Correction necessary for some reason
        this.height = height;
    }

    public void registerWidth(char character, int width) {
        if (widths == null) { widths = new HashMap<>(); }
        widths.put(character, width);
    }

    public int getWidth(char character, boolean custom) {
        int result = this == DEFAULT ?
                widths.getOrDefault(character, 6) :
                widths.getOrDefault(character, DEFAULT.getWidth(character, custom));
        if (!custom) {
            result = Math.round(result*height/8f); // Scale
        }
        return result;
    }

    public int getWidth(String text) {
        if (text.equals("")) { return 0; }
        int total = 0;
        for (char character : text.toCharArray()) { total += getWidth(character, false); }
        return total;
    }

    @Override
    public String toString() { return name; }

    public String getName() { return name; }

    public int getHeight() { return height; }
}
