package com.sentropic.guiapi.gui;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    protected final int left;
    protected final int right;

    /**
     * @param id the identifier of this GUIComponent
     * @param component the {@link BaseComponent} containing the text and formatting of this GUIComponent
     * @param offset the lateral offset of this GUIComponent in the player's screen
     * @param alignment the alignment to set this GUIComponent to,
     *                  whether {@link Alignment#LEFT}, {@link Alignment#RIGHT} or {@link Alignment#CENTER}
     * @param scale whether the text in the provided {@link BaseComponent} should be scaled according to its fonts
     * @throws IllegalArgumentException if the provided {@link BaseComponent} is not supported,
     * according to the criteria of {@link GUIComponent#check(BaseComponent)}
     */
    public GUIComponent(String id, BaseComponent component, int offset, Alignment alignment, boolean scale) {
        this(id, component, getWidth(component, scale), offset, alignment);
    }

    /**
     *
     * @param id the identifier of this GUIComponent
     * @param component the {@link BaseComponent} containing the text and formatting of this GUIComponent
     * @param width the total width of the provided {@link BaseComponent} in the player's screen
     * @param offset the lateral offset of this GUIComponent in the player's screen
     * @param alignment the alignment to set this GUIComponent to,
     *                  whether {@link Alignment#LEFT}, {@link Alignment#RIGHT} or {@link Alignment#CENTER}
     * @throws IllegalArgumentException if the provided {@link BaseComponent} is not supported,
     * according to the criteria of {@link GUIComponent#check(BaseComponent)}
     */
    public GUIComponent(String id, BaseComponent component, int width, int offset, Alignment alignment) {
        check(component);
        this.id = id;
        this.component = component.duplicate();
        int[] spaces = spacesFor(width, offset, alignment);
        left = spaces[0];
        right = spaces[1];
    }

    /**
     * Checks whether the given {@link BaseComponent} is supported
     * (contains components only of type {@link TextComponent} and non-bold text)
     * @param component the component to be checked
     */
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

    /**
     * Calculates the width the given {@link BaseComponent} would have in-screen
     * @param component the component to calculate the width for
     * @param scale whether the component's text should be scaled according to its fonts
     * @return the calculated width the component would have in-screen
     */
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

    /**
     * Calculates the amount of space to be placed before and after a {@link GUIComponent},
     * given its width, lateral offset and {@link Alignment}
     * @param width the width of the component
     * @param offset the lateral offset of the component
     * @param alignment the alignment of the component,
     *                  whether {@link Alignment#LEFT}, {@link Alignment#RIGHT} or {@link Alignment#CENTER}
     * @return an 2 int array containing the left side spaces in its first index, and the right side spaces in its second
     */
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

    /**
     * Gets the ID of this component
     * @return the {@link String} representing the component's ID
     */
    public String getID() { return id; }

    /**
     * For internal ose only. Must not be modified
     * @return the {@link BaseComponent} contained by this GUIComponent
     */
    BaseComponent getComponent() { return component; }

    /**
     * @return the space to be placed before this component
     */
    public int getLeftSpaces() { return left; }

    /**
     * @return the space to be placed after this component
     */
    public int getRightSpaces() { return right; }

    void onAdd(GUI gui) { }

    void onRemove(GUI gui) { }

    /**
     * Creates a text component using the given text and font.
     * Use inside the GUIComponent constructor to create it with a single statement.
     * @param text the text contained by the TextComponent
     * @param font the namespaced font ID to use for the text, as used in the resource pack
     * @return a new TextComponent with the provided text and font
     */
    @NotNull
    public static TextComponent createTextComponent(@Nullable String text, @Nullable String font) {
        if (text == null) { text = ""; }
        TextComponent textComponent = new TextComponent(text);
        textComponent.setFont(font);
        return textComponent;
    }
}
