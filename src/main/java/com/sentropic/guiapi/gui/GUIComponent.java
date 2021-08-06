package com.sentropic.guiapi.gui;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GUIComponent {
    private String id;
    private final String text;
    private final Font font;
    private final int leftOffset;
    private final int rightOffset;

    public enum Alignment {LEFT, RIGHT, CENTERED}

    public GUIComponent(@NotNull String id,
                        int offset,
                        char character,
                        Font font,
                        @NotNull Alignment alignment) {
        this.id = id;
        this.text = String.valueOf(character);
        this.font = font;

        int width = font.getWidth(character, true);
        switch (alignment) {
            case LEFT:
                this.leftOffset = offset;
                this.rightOffset = -offset-width;
                break;
            case RIGHT:
                this.leftOffset = offset-width;
                this.rightOffset = -offset;
                break;
            case CENTERED:
                this.leftOffset = offset-width/2;
                this.rightOffset = -this.leftOffset-width;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public GUIComponent(@NotNull String id,
                        int offset,
                        @NotNull String text,
                        Font font,
                        @NotNull Alignment alignment) {
        this.id = id;
        this.text = text;
        this.font = font;
        switch (alignment) {
            case LEFT:
                this.leftOffset = offset;
                this.rightOffset = -offset-font.getWidth(text);
                break;
            case RIGHT:
                this.leftOffset = offset-font.getWidth(text);
                this.rightOffset = -offset;
                break;
            case CENTERED:
                int width = font.getWidth(text);
                this.leftOffset = offset-width/2;
                this.rightOffset = -this.leftOffset-width;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    private GUIComponent(int leftOffset, String text, Font font, int rightOffset) {
        this.leftOffset = leftOffset;
        this.text = text;
        this.font = font;
        this.rightOffset = rightOffset;
    }

    public List<GUIComponent> byWord() {
        List<GUIComponent> list = new ArrayList<>();
        String[] words = text.split(" ");
        if (words.length == 1) {
            list.add(this);
            return list;
        }
        int spaces = (int) Math.round(4.0*font.getHeight()/8);
        list.add(new GUIComponent(leftOffset, words[0], font, 0));
        for (int i = 1; i < words.length-1; i++) {
            list.add(new GUIComponent(spaces, words[i], font, 0));
        }
        list.add(new GUIComponent(spaces, words[words.length-1], font, rightOffset));
        return list;
    }

    public String getId() { return id; }

    public String getText() { return text; }

    public Font getFont() { return font == null ? Font.DEFAULT : font; }

    public int getLeftOffset() { return leftOffset; }

    public int getRightOffset() { return rightOffset; }
}
