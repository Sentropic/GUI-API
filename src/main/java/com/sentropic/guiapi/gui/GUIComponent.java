package com.sentropic.guiapi.gui;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GUIComponent {
    private static final Set<Class<? extends BaseComponent>> validComponents = new HashSet<Class<? extends BaseComponent>>() {{
        add(TextComponent.class);
        // TODO see if can include Score and Selector
        // TODO add support for bold text
    }};

    protected final String id;
    protected final BaseComponent component;
    protected final int leftOffset;
    protected final int rightOffset;

    public GUIComponent(String id, BaseComponent component, int offset, Alignment alignment, boolean scale) {
        this(id, component, getWidth(component, scale), offset, alignment);
    }

    public GUIComponent(String id, BaseComponent component, int width, int offset, Alignment alignment) {
        check(component);
        this.id = id;
        this.component = component.duplicate();
        int[] spaces = spacesFor(width, offset, alignment);
        leftOffset = spaces[0];
        rightOffset = spaces[1];
    }

    public static void check(BaseComponent component) {
        if (component.isBold() || !validComponents.contains(component.getClass())) {
            throw new IllegalArgumentException();
        }
        List<BaseComponent> extras = component.getExtra();
        if (extras != null) {
            for (BaseComponent extra : extras) {
                if (extra == component) { throw new IllegalArgumentException(); }
                check(extra);
            }
        }
    }

    public static int getWidth(BaseComponent component, boolean scale) {
        int result = 0;
        Font font = Font.getRegistered(component.getFont());
        if (font == null) { font = Font.DEFAULT; }
        if (component instanceof TextComponent) {
            TextComponent textComponent = (TextComponent) component;
            result += font.getWidth(textComponent.getText(), scale);
        }
        List<BaseComponent> extras = component.getExtra();
        if (extras != null) {
            for (BaseComponent extra : extras) {
                result += getWidth(extra, scale);
            }
        }
        return result;
    }

    public static int[] spacesFor(int width, int offset, Alignment alignment) {
        int[] result = new int[2];
        switch (alignment) {
            case LEFT:
                result[0] = offset;
                result[1] = -offset-width;
                break;
            case RIGHT:
                result[0] = offset-width;
                result[1] = -offset;
                break;
            case CENTER:
                result[0] = offset-width/2;
                result[1] = -result[0]-width;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return result;
    }

    public String getID() { return id; }

    BaseComponent getComponent() { return component; }

    public int getLeftOffset() { return leftOffset; }

    public int getRightOffset() { return rightOffset; }

    public void onAdd(GUI gui) { }

    public void onRemove(GUI gui) { }
}
