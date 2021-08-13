package com.sentropic.guiapi.gui;

import org.jetbrains.annotations.NotNull;

public class GUIComponent {
    private String id;
    private final String text;
    private final Font font;
    private final int leftOffset;
    private final int rightOffset;

    public GUIComponent(@NotNull String id,
                        int offset,
                        @NotNull String text,
                        Font font,
                        @NotNull Alignment alignment,
                        boolean scale) {
        this(id, offset, text, font.getWidth(text, scale), font, alignment);
    }

    GUIComponent(String id,
                 int offset,
                 String text,
                 int width,
                 Font font,
                 Alignment alignment) {
        this.id = id;
        this.text = text;
        this.font = font;
        switch (alignment) {
            case LEFT:
                this.leftOffset = offset;
                this.rightOffset = -offset-width;
                break;
            case RIGHT:
                this.leftOffset = offset-width;
                this.rightOffset = -offset;
                break;
            case CENTER:
                this.leftOffset = offset-width/2;
                this.rightOffset = -this.leftOffset-width;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getID() { return id; }

    public String getText() { return text; }

    public Font getFont() { return font == null ? Font.DEFAULT : font; }

    public int getLeftOffset() { return leftOffset; }

    public int getRightOffset() { return rightOffset; }
}
